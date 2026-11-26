package com.tiet.journeyconnect.services;

import com.tiet.journeyconnect.dto.TravelDtos.*;
import com.tiet.journeyconnect.exceptions.OverbookingException;
import com.tiet.journeyconnect.exceptions.SingleRidePolicyViolationException;
import com.tiet.journeyconnect.exceptions.SlotNotFoundException;
import com.tiet.journeyconnect.models.RideSlot;
import com.tiet.journeyconnect.models.RideSlot.SlotMemberView;
import com.tiet.journeyconnect.models.entities.*;
import com.tiet.journeyconnect.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Core 15-Minute Slot Engine for Thapar Journey Connect.
 * Demonstrates:
 *  - Java Collections Framework: ConcurrentHashMap<String, RideSlot> for O(1) thread-safe lookups,
 *    and TreeSet<RideSlot> implementing Comparable for chronological ordering.
 *  - Multithreading & Concurrency Control: ReentrantLock guarding critical booking sections.
 *  - Academic Concurrency Protection: Prevents overbooking beyond vehicle capacity.
 */
@Service
public class SlotEngineService {

    private static final ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");

    // Thread-safe concurrent cache for O(1) slot lookups
    private final ConcurrentHashMap<String, RideSlot> slotCache = new ConcurrentHashMap<>();

    // Fine-grained lock registry for critical section protection per slot key
    private final ConcurrentHashMap<String, ReentrantLock> slotLocks = new ConcurrentHashMap<>();

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private CityRideRepository cityRideRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private DestinationRepository destinationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private SerializerService serializerService;

    @Autowired
    private RidePolicyService ridePolicyService;

    @Autowired
    private NotificationService notificationService;

    private ReentrantLock getLock(String key) {
        return slotLocks.computeIfAbsent(key, k -> new ReentrantLock());
    }

    /**
     * Generates all 96 discrete 15-minute intervals across 24 hours (00:00 to 23:45)
     * for the given destination, date, vehicle and direction.
     * Uses TreeSet<RideSlot> to sort chronologically.
     */
    public List<RideSlot> generate96Slots(String destinationId, String dateStr, String vehicle, String direction, String currentUserId) {
        String travelDate = normalizeDate(dateStr);
        boolean isReturn = "returning".equalsIgnoreCase(direction);
        String tripDirection = isReturn ? "returning" : "departing";
        String selectedVehicle = (vehicle == null || vehicle.isEmpty() || "Either".equalsIgnoreCase(vehicle)) ? "Shared Cab" : vehicle;

        Optional<DestinationEntity> destOpt = destinationRepository.findById(destinationId);
        String destName = destOpt.map(DestinationEntity::getName).orElse(destinationId);
        String originName = isReturn ? destName : "Thapar Institute";
        String targetDestName = isReturn ? "Thapar Institute" : destName;

        // Fetch existing active groups for this route
        List<GroupEntity> existingGroups = groupRepository.findByDestinationIdAndDateAndStatusIn(
                destinationId, travelDate, List.of("Open", "Confirmed")
        );

        Map<String, GroupEntity> groupsByTime = new HashMap<>();
        for (GroupEntity g : existingGroups) {
            if (tripDirection.equalsIgnoreCase(g.getDirection())) {
                List<GroupMemberEntity> members = groupMemberRepository.findByTargetIdAndGroupTypeOrderByJoinedAtAsc(g.getId(), "CAMPUS_GROUP");
                int max = g.getMaxMembers() != null ? g.getMaxMembers() : 4;
                boolean joinable = "Open".equalsIgnoreCase(g.getStatus()) && members.size() < max;
                GroupEntity prev = groupsByTime.get(g.getDepartureTime());
                if (prev == null || joinable) {
                    groupsByTime.put(g.getDepartureTime(), g);
                }
            }
        }

        // Use TreeSet to maintain chronological ordering
        TreeSet<RideSlot> sortedSlots = new TreeSet<>();

        LocalDateTime nowIST = LocalDateTime.now(IST_ZONE);

        for (int h = 0; h < 24; h++) {
            for (int m = 0; m < 60; m += 15) {
                String timeStr = String.format("%02d:%02d", h, m);
                double hoursLeft = getHoursUntilDeparture(travelDate, timeStr, nowIST);
                boolean isPast = hoursLeft < (-5.0 / 60.0);

                GroupEntity group = groupsByTime.get(timeStr);
                String slotVehicle = (group != null && group.getTransport() != null) ? group.getTransport() : selectedVehicle;

                String defaultSlotKey = isReturn
                        ? destinationId + "_ret_" + travelDate + "_" + timeStr + "_" + slotVehicle
                        : destinationId + "_" + travelDate + "_" + timeStr + "_" + slotVehicle;

                String slotKey = (group != null && group.getSlotKey() != null) ? group.getSlotKey() : defaultSlotKey;

                RideSlot slot = new RideSlot(
                        slotKey,
                        timeStr,
                        travelDate,
                        destinationId,
                        targetDestName,
                        originName,
                        tripDirection,
                        slotVehicle
                );

                slot.setPast(isPast);

                if (group != null) {
                    slot.setGroupId(group.getId());
                    slot.setStatus(group.getStatus());
                    slot.setMaxSeats(group.getMaxMembers() != null ? group.getMaxMembers() : 4);

                    List<GroupMemberEntity> members = groupMemberRepository.findByTargetIdAndGroupTypeOrderByJoinedAtAsc(group.getId(), "CAMPUS_GROUP");
                    List<SlotMemberView> views = new ArrayList<>();
                    boolean joined = false;

                    for (GroupMemberEntity mem : members) {
                        boolean isSelf = currentUserId != null && currentUserId.equals(mem.getUserId());
                        if (isSelf) joined = true;

                        String displayName = isSelf ? mem.getName() : (mem.getName() != null ? mem.getName().split(" ")[0] : "Rider");
                        views.add(new SlotMemberView(
                                isSelf ? mem.getUserId() : null,
                                displayName,
                                mem.getAvatar(),
                                isSelf ? mem.getHostel() : null,
                                mem.getRole(),
                                mem.getReliability() != null ? mem.getReliability() : 5.0
                        ));
                    }

                    slot.setMembersList(views);
                    slot.setJoined(joined);
                } else {
                    slot.setStatus("Open");
                    slot.setMaxSeats(4);
                    slot.setSeatsFilled(0);
                    slot.setFull(false);
                    slot.setJoined(false);
                }

                sortedSlots.add(slot);
                slotCache.put(slotKey, slot);
            }
        }

        return new ArrayList<>(sortedSlots);
    }

    /**
     * Synchronized and atomic slot join with critical section protection.
     * Prevents race conditions when multiple riders simultaneously book the last seat.
     */
    @Transactional
    public Map<String, Object> joinSlot(JoinSlotRequest req, UserEntity user) {
        if (req.getDestinationId() == null || req.getTime() == null) {
            throw new IllegalArgumentException("Destination and departure time are required.");
        }

        String travelDate = normalizeDate(req.getDate());
        String time = req.getTime().trim();
        boolean isReturn = "returning".equalsIgnoreCase(req.getDirection());
        String tripDirection = isReturn ? "returning" : "departing";
        String selectedVehicle = ("Shared Auto".equalsIgnoreCase(req.getVehicle())) ? "Shared Auto" : "Shared Cab";

        // Validate 15-minute slot boundary
        String[] parts = time.split(":");
        if (parts.length != 2 || Integer.parseInt(parts[1]) % 15 != 0) {
            throw new IllegalArgumentException("Select a valid 15-minute departure slot.");
        }

        // Validate future departure
        double hoursLeft = getHoursUntilDeparture(travelDate, time, LocalDateTime.now(IST_ZONE));
        if (hoursLeft < (-5.0 / 60.0)) {
            throw new IllegalArgumentException("Cannot book a travel slot in the past.");
        }

        String slotKey = isReturn
                ? req.getDestinationId() + "_ret_" + travelDate + "_" + time + "_" + selectedVehicle
                : req.getDestinationId() + "_" + travelDate + "_" + time + "_" + selectedVehicle;

        // Acquire lock on the slot key to guard against simultaneous bookings
        ReentrantLock lock = getLock(slotKey);
        lock.lock();
        try {
            // Check if active group already exists for this slot
            List<GroupEntity> activeGroups = groupRepository.findByDestinationIdAndDateAndStatusIn(
                    req.getDestinationId(), travelDate, List.of("Open", "Confirmed")
            );

            GroupEntity existingGroup = null;
            for (GroupEntity candidate : activeGroups) {
                if (time.equals(candidate.getDepartureTime()) &&
                        tripDirection.equalsIgnoreCase(candidate.getDirection()) &&
                        selectedVehicle.equalsIgnoreCase(candidate.getTransport())) {
                    existingGroup = candidate;
                    break;
                }
            }

            if (existingGroup != null) {
                List<GroupMemberEntity> currentMembers = groupMemberRepository.findByTargetIdAndGroupTypeOrderByJoinedAtAsc(existingGroup.getId(), "CAMPUS_GROUP");
                boolean alreadyMember = currentMembers.stream().anyMatch(m -> user.getId().equals(m.getUserId()));
                if (alreadyMember) {
                    Map<String, Object> result = new LinkedHashMap<>();
                    result.put("success", true);
                    result.put("message", "You are already a member of Group #" + existingGroup.getId() + ".");
                    result.put("group", serializerService.serializeGroup(existingGroup, currentMembers, user.getId()));
                    result.put("created", false);
                    return result;
                }
            }

            // Enforce Campus-wide Single-Ride Policy across all categories
            ridePolicyService.validateSingleRidePolicy(user.getId(), existingGroup != null ? existingGroup.getId() : null);

            boolean isCreated = false;
            GroupEntity targetGroup;

            if (existingGroup != null) {
                List<GroupMemberEntity> currentMembers = groupMemberRepository.findByTargetIdAndGroupTypeOrderByJoinedAtAsc(existingGroup.getId(), "CAMPUS_GROUP");
                int maxCap = existingGroup.getMaxMembers() != null ? existingGroup.getMaxMembers() : 4;
                if (currentMembers.size() >= maxCap) {
                    throw new OverbookingException(currentMembers.size(), maxCap);
                }
                targetGroup = existingGroup;
            } else {
                // Lazy atomic group creation with sequential numerical ID
                Integer maxId = groupRepository.findMaxNumericId();
                int nextId = (maxId != null ? maxId : 0) + 1;

                Optional<DestinationEntity> destOpt = destinationRepository.findById(req.getDestinationId());
                String destName = destOpt.map(DestinationEntity::getName).orElse(req.getDestinationId());

                targetGroup = new GroupEntity();
                targetGroup.setId(String.valueOf(nextId));
                targetGroup.setSlotKey(slotKey);
                targetGroup.setDestinationId(req.getDestinationId());
                targetGroup.setDestinationName(isReturn ? "Thapar Institute" : destName);
                targetGroup.setOrigin(isReturn ? destName : "Thapar Institute");
                targetGroup.setDirection(tripDirection);
                targetGroup.setDepartureTime(time);
                targetGroup.setDate(travelDate);
                targetGroup.setStatus("Open");
                targetGroup.setTransport(selectedVehicle);
                targetGroup.setMaxMembers(4);
                targetGroup.setPickupPoint("Main Gate");
                targetGroup.setAdminId(user.getId());
                targetGroup = groupRepository.save(targetGroup);
                isCreated = true;

                // Create initial chat welcome message
                ChatMessageEntity welcomeMsg = new ChatMessageEntity();
                welcomeMsg.setId(UUID.randomUUID().toString());
                welcomeMsg.setGroupId(targetGroup.getId());
                welcomeMsg.setSenderId(user.getId());
                welcomeMsg.setSenderName(user.getName());
                welcomeMsg.setSenderAvatar(user.getAvatar());
                welcomeMsg.setSenderHostel(user.getHostel());
                welcomeMsg.setText("Group #" + targetGroup.getId() + " formed for " + targetGroup.getDestinationName() + " at " + time + ". Welcome co-riders!");
                chatMessageRepository.save(welcomeMsg);
            }

            // Add new member
            GroupMemberEntity newMember = new GroupMemberEntity();
            newMember.setId(UUID.randomUUID().toString());
            newMember.setTargetId(targetGroup.getId());
            newMember.setGroupType("CAMPUS_GROUP");
            newMember.setUserId(user.getId());
            newMember.setName(user.getName());
            newMember.setAvatar(user.getAvatar());
            newMember.setHostel(user.getHostel());
            newMember.setReliability(user.getReliability());
            newMember.setDepartureTime(time);
            newMember.setRole(isCreated ? "Group Admin" : "Member");
            newMember.setPhone(user.getPhone());
            newMember.setJoinedAt(Instant.now());
            groupMemberRepository.save(newMember);

            user.setActiveGroupId(targetGroup.getId());
            userRepository.save(user);

            List<GroupMemberEntity> updatedMembers = groupMemberRepository.findByTargetIdAndGroupTypeOrderByJoinedAtAsc(targetGroup.getId(), "CAMPUS_GROUP");

            if (!isCreated) {
                notificationService.broadcastMemberJoined(
                        targetGroup.getId(),
                        "CAMPUS_GROUP",
                        targetGroup.getDestinationName(),
                        user,
                        updatedMembers.size(),
                        targetGroup.getMaxMembers() != null ? targetGroup.getMaxMembers() : 4
                );
            }

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("success", true);
            result.put("message", isCreated
                    ? "Formed Group #" + targetGroup.getId() + " for " + targetGroup.getDestinationName() + " at " + time + "!"
                    : "Successfully joined Group #" + targetGroup.getId() + " for " + targetGroup.getDestinationName() + " at " + time + ".");
            result.put("group", serializerService.serializeGroup(targetGroup, updatedMembers, user.getId()));
            result.put("created", isCreated);
            return result;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Preview reliability penalty for rescheduling old group.
     */
    public Map<String, Object> previewReschedule(String oldGroupId, UserEntity user) {
        if (oldGroupId == null || oldGroupId.trim().isEmpty()) {
            throw new IllegalArgumentException("Current group ID is required.");
        }

        Optional<GroupEntity> groupOpt = groupRepository.findById(oldGroupId);
        if (groupOpt.isEmpty()) {
            throw new SlotNotFoundException("Current group not found.");
        }

        GroupEntity oldGroup = groupOpt.get();
        List<GroupMemberEntity> members = groupMemberRepository.findByTargetIdAndGroupTypeOrderByJoinedAtAsc(oldGroup.getId(), "CAMPUS_GROUP");
        boolean isMember = members.stream().anyMatch(m -> user.getId().equals(m.getUserId()));
        if (!isMember) {
            throw new IllegalArgumentException("Only enrolled members can preview a change.");
        }

        double hoursLeft = getHoursUntilDeparture(oldGroup.getDate(), oldGroup.getDepartureTime(), LocalDateTime.now(IST_ZONE));
        double penalty = 0.0;
        if (hoursLeft >= 0.0 && hoursLeft <= 0.5) {
            penalty = 0.8;
        } else if (hoursLeft >= 0.0 && hoursLeft <= 4.0) {
            penalty = 0.3;
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", true);
        result.put("hoursLeft", Math.max(0.0, Math.round(hoursLeft * 100.0) / 100.0));
        result.put("penalty", penalty);
        result.put("willIncurPenalty", penalty > 0.0);
        result.put("oldDepartureTime", oldGroup.getDepartureTime());
        result.put("oldDate", oldGroup.getDate());
        return result;
    }

    /**
     * Atomic Rescheduling with reliability penalty and rollback safety.
     */
    @Transactional
    public Map<String, Object> rescheduleSlot(RescheduleSlotRequest req, UserEntity user) {
        if (req.getOldGroupId() == null || req.getTargetTime() == null) {
            throw new IllegalArgumentException("Current group ID and target departure time are required.");
        }

        Optional<GroupEntity> oldGroupOpt = groupRepository.findById(req.getOldGroupId());
        if (oldGroupOpt.isEmpty()) {
            throw new SlotNotFoundException("Current group not found.");
        }

        GroupEntity oldGroup = oldGroupOpt.get();
        List<GroupMemberEntity> oldMembers = groupMemberRepository.findByTargetIdAndGroupTypeOrderByJoinedAtAsc(oldGroup.getId(), "CAMPUS_GROUP");
        Optional<GroupMemberEntity> memberOpt = oldMembers.stream().filter(m -> user.getId().equals(m.getUserId())).findFirst();
        if (memberOpt.isEmpty()) {
            throw new IllegalArgumentException("You are not a member of this travel group.");
        }

        String targetDestId = (req.getTargetDestinationId() != null) ? req.getTargetDestinationId() : oldGroup.getDestinationId();
        String targetDate = normalizeDate(req.getTargetDate() != null ? req.getTargetDate() : oldGroup.getDate());
        String targetTime = req.getTargetTime().trim();
        String targetVehicle = (req.getTargetVehicle() != null) ? req.getTargetVehicle() : oldGroup.getTransport();
        boolean isTargetReturn = "returning".equalsIgnoreCase(req.getTargetDirection()) || (req.getTargetDirection() == null && "returning".equalsIgnoreCase(oldGroup.getDirection()));
        String targetDirection = isTargetReturn ? "returning" : "departing";

        // Check identical slot
        if (oldGroup.getDestinationId().equals(targetDestId) &&
                oldGroup.getDate().equals(targetDate) &&
                oldGroup.getDepartureTime().equals(targetTime) &&
                oldGroup.getTransport().equalsIgnoreCase(targetVehicle) &&
                Objects.equals(oldGroup.getDirection(), targetDirection)) {
            throw new IllegalArgumentException("You are already in this travel slot.");
        }

        double hoursLeftOld = getHoursUntilDeparture(oldGroup.getDate(), oldGroup.getDepartureTime(), LocalDateTime.now(IST_ZONE));
        double penalty = 0.0;
        if (hoursLeftOld >= 0.0 && hoursLeftOld <= 0.5) {
            penalty = 0.8;
        } else if (hoursLeftOld >= 0.0 && hoursLeftOld <= 4.0) {
            penalty = 0.3;
        }

        double newReliability = user.getReliability() != null ? user.getReliability() : 5.0;
        if (penalty > 0.0) {
            newReliability = Math.max(1.0, Math.round((newReliability - penalty) * 10.0) / 10.0);
        }

        // 1. Join Target Slot
        JoinSlotRequest joinReq = new JoinSlotRequest();
        joinReq.setDestinationId(targetDestId);
        joinReq.setDate(targetDate);
        joinReq.setTime(targetTime);
        joinReq.setVehicle(targetVehicle);
        joinReq.setDirection(targetDirection);

        // Temporarily clear activeGroupId to allow atomic reschedule
        user.setActiveGroupId(null);
        userRepository.save(user);

        // 2. Remove from old group
        groupMemberRepository.deleteByTargetIdAndUserId(oldGroup.getId(), user.getId());
        List<GroupMemberEntity> remainingOldMembers = groupMemberRepository.findByTargetIdAndGroupTypeOrderByJoinedAtAsc(oldGroup.getId(), "CAMPUS_GROUP");

        if (remainingOldMembers.isEmpty()) {
            groupRepository.delete(oldGroup);
            chatMessageRepository.deleteByGroupId(oldGroup.getId());
        } else {
            // Reassign admin if leaver was admin
            if (user.getId().equals(oldGroup.getAdminId())) {
                GroupMemberEntity nextAdmin = remainingOldMembers.get(0);
                oldGroup.setAdminId(nextAdmin.getUserId());
                nextAdmin.setRole("Group Admin");
                groupMemberRepository.save(nextAdmin);
                groupRepository.save(oldGroup);
            }

            ChatMessageEntity leaveAlert = new ChatMessageEntity();
            leaveAlert.setId(UUID.randomUUID().toString());
            leaveAlert.setGroupId(oldGroup.getId());
            leaveAlert.setSenderId("system");
            leaveAlert.setSenderName("Policy Alert");
            leaveAlert.setSenderAvatar("PA");
            leaveAlert.setText("⚠️ Notice: " + user.getName() + " rescheduled their journey to another travel slot." + (penalty > 0 ? " (Late rescheduling penalty: -" + penalty + "⭐)" : ""));
            chatMessageRepository.save(leaveAlert);

            notificationService.broadcastMemberLeft(
                    oldGroup.getId(),
                    "CAMPUS_GROUP",
                    oldGroup.getDestinationName(),
                    user,
                    remainingOldMembers.size(),
                    oldGroup.getMaxMembers() != null ? oldGroup.getMaxMembers() : 4
            );
        }

        // 3. Join target group
        Map<String, Object> joinResult = joinSlot(joinReq, user);

        // 4. Update user reliability
        user.setReliability(newReliability);
        userRepository.save(user);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", true);
        result.put("message", "Successfully rescheduled your journey!");
        result.put("group", joinResult.get("group"));
        result.put("penaltyApplied", penalty > 0.0);
        result.put("penaltyAmount", penalty);
        result.put("newReliability", newReliability);
        return result;
    }

    public static String normalizeDate(String input) {
        if (input == null || input.trim().isEmpty() || "Today".equalsIgnoreCase(input.trim())) {
            return LocalDate.now(IST_ZONE).toString();
        }
        if ("Tomorrow".equalsIgnoreCase(input.trim())) {
            return LocalDate.now(IST_ZONE).plusDays(1).toString();
        }
        return input.trim();
    }

    public static double getHoursUntilDeparture(String dateStr, String timeStr, LocalDateTime now) {
        try {
            LocalDate date = LocalDate.parse(dateStr);
            LocalTime time = LocalTime.parse(timeStr);
            LocalDateTime departure = LocalDateTime.of(date, time);
            long minutes = ChronoUnit.MINUTES.between(now, departure);
            return minutes / 60.0;
        } catch (Exception e) {
            return 0.0;
        }
    }
}
