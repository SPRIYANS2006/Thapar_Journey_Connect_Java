package com.tiet.journeyconnect.services;

import com.tiet.journeyconnect.dto.TravelDtos.*;
import com.tiet.journeyconnect.exceptions.OverbookingException;
import com.tiet.journeyconnect.exceptions.SingleRidePolicyViolationException;
import com.tiet.journeyconnect.exceptions.SlotNotFoundException;
import com.tiet.journeyconnect.exceptions.UnauthorizedAccessException;
import com.tiet.journeyconnect.models.entities.*;
import com.tiet.journeyconnect.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class GroupService {

    private static final ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private CityRideRepository cityRideRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private RideRecordRepository rideRecordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SerializerService serializerService;

    @Autowired
    private SlotEngineService slotEngineService;

    public List<Map<String, Object>> getMyGroups(UserEntity user) {
        List<GroupMemberEntity> memberships = groupMemberRepository.findByUserId(user.getId());
        List<Map<String, Object>> merged = new ArrayList<>();

        for (GroupMemberEntity mem : memberships) {
            if ("CAMPUS_GROUP".equals(mem.getGroupType())) {
                Optional<GroupEntity> gOpt = groupRepository.findById(mem.getTargetId());
                if (gOpt.isPresent()) {
                    GroupEntity g = gOpt.get();
                    if ("Open".equalsIgnoreCase(g.getStatus()) || "Confirmed".equalsIgnoreCase(g.getStatus())) {
                        List<GroupMemberEntity> allMembers = groupMemberRepository.findByTargetIdAndGroupTypeOrderByJoinedAtAsc(g.getId(), "CAMPUS_GROUP");
                        merged.add(serializerService.serializeGroup(g, allMembers, user.getId()));
                    }
                }
            } else if ("CITY_RIDE".equals(mem.getGroupType())) {
                Optional<CityRideEntity> rOpt = cityRideRepository.findById(mem.getTargetId());
                if (rOpt.isPresent()) {
                    CityRideEntity r = rOpt.get();
                    if ("Open".equalsIgnoreCase(r.getStatus()) || "Full".equalsIgnoreCase(r.getStatus()) || "Confirmed".equalsIgnoreCase(r.getStatus())) {
                        List<GroupMemberEntity> allMembers = groupMemberRepository.findByTargetIdAndGroupTypeOrderByJoinedAtAsc(r.getId(), "CITY_RIDE");
                        merged.add(serializerService.serializeCityRideAsGroup(r, allMembers, user.getId()));
                    }
                }
            }
        }

        return merged;
    }

    public List<Map<String, Object>> getGroupHistory(UserEntity user) {
        List<GroupMemberEntity> memberships = groupMemberRepository.findByUserId(user.getId());
        List<Map<String, Object>> history = new ArrayList<>();

        for (GroupMemberEntity mem : memberships) {
            if ("CAMPUS_GROUP".equals(mem.getGroupType())) {
                Optional<GroupEntity> gOpt = groupRepository.findById(mem.getTargetId());
                if (gOpt.isPresent()) {
                    GroupEntity g = gOpt.get();
                    if ("Completed".equalsIgnoreCase(g.getStatus()) || "Cancelled".equalsIgnoreCase(g.getStatus())) {
                        List<GroupMemberEntity> allMembers = groupMemberRepository.findByTargetIdAndGroupTypeOrderByJoinedAtAsc(g.getId(), "CAMPUS_GROUP");
                        history.add(serializerService.serializeGroup(g, allMembers, user.getId()));
                    }
                }
            } else if ("CITY_RIDE".equals(mem.getGroupType())) {
                Optional<CityRideEntity> rOpt = cityRideRepository.findById(mem.getTargetId());
                if (rOpt.isPresent()) {
                    CityRideEntity r = rOpt.get();
                    if ("Completed".equalsIgnoreCase(r.getStatus()) || "Cancelled".equalsIgnoreCase(r.getStatus())) {
                        List<GroupMemberEntity> allMembers = groupMemberRepository.findByTargetIdAndGroupTypeOrderByJoinedAtAsc(r.getId(), "CITY_RIDE");
                        history.add(serializerService.serializeCityRideAsGroup(r, allMembers, user.getId()));
                    }
                }
            }
        }

        return history;
    }

    public Map<String, Object> getGroupById(String id, UserEntity user) {
        if (id.startsWith("cr_")) {
            Optional<CityRideEntity> rideOpt = cityRideRepository.findById(id);
            if (rideOpt.isEmpty()) {
                throw new SlotNotFoundException("Group not found.");
            }
            CityRideEntity ride = rideOpt.get();
            List<GroupMemberEntity> members = groupMemberRepository.findByTargetIdAndGroupTypeOrderByJoinedAtAsc(ride.getId(), "CITY_RIDE");
            boolean isMember = members.stream().anyMatch(m -> user.getId().equals(m.getUserId()));
            if (!isMember) {
                throw new UnauthorizedAccessException("Forbidden: Group details and rider contacts are restricted to enrolled group members.", 403);
            }
            return serializerService.serializeCityRideAsGroup(ride, members, user.getId());
        }

        Optional<GroupEntity> groupOpt = groupRepository.findById(id);
        if (groupOpt.isEmpty()) {
            throw new SlotNotFoundException("Group not found.");
        }

        GroupEntity group = groupOpt.get();
        List<GroupMemberEntity> members = groupMemberRepository.findByTargetIdAndGroupTypeOrderByJoinedAtAsc(group.getId(), "CAMPUS_GROUP");
        boolean isMember = members.stream().anyMatch(m -> user.getId().equals(m.getUserId()));
        if (!isMember) {
            throw new UnauthorizedAccessException("Forbidden: Group details and rider contacts are restricted to enrolled group members.", 403);
        }

        return serializerService.serializeGroup(group, members, user.getId());
    }

    @Transactional
    public Map<String, Object> joinGroup(String id, UserEntity user) {
        if (id.startsWith("cr_")) {
            Optional<CityRideEntity> rideOpt = cityRideRepository.findById(id);
            if (rideOpt.isEmpty()) {
                throw new SlotNotFoundException("Group not found.");
            }
            CityRideEntity ride = rideOpt.get();

            if (!ride.getCommunityId().equals(user.getJoinedCommunity())) {
                throw new UnauthorizedAccessException("Forbidden: You can only join rides within your enrolled hometown community.", 403);
            }

            List<GroupMemberEntity> members = groupMemberRepository.findByTargetIdAndGroupTypeOrderByJoinedAtAsc(ride.getId(), "CITY_RIDE");
            if (members.stream().anyMatch(m -> user.getId().equals(m.getUserId()))) {
                throw new IllegalArgumentException("You are already a member of this ride.");
            }

            if (members.size() >= ride.getMaxCapacity()) {
                throw new OverbookingException("This ride is already full.");
            }

            GroupMemberEntity mem = new GroupMemberEntity();
            mem.setId(UUID.randomUUID().toString());
            mem.setTargetId(ride.getId());
            mem.setGroupType("CITY_RIDE");
            mem.setUserId(user.getId());
            mem.setName(user.getName());
            mem.setAvatar(user.getAvatar());
            mem.setHostel(user.getHostel());
            mem.setReliability(user.getReliability());
            mem.setPhone(user.getPhone());
            mem.setRole("Member");
            mem.setJoinedAt(Instant.now());
            groupMemberRepository.save(mem);

            members.add(mem);
            ride.setCurrentMembersCount(members.size());
            if (members.size() >= ride.getMaxCapacity()) {
                ride.setStatus("Full");
            }
            cityRideRepository.save(ride);

            Map<String, Object> res = new LinkedHashMap<>();
            res.put("success", true);
            res.put("group", serializerService.serializeCityRideAsGroup(ride, members, user.getId()));
            return res;
        }

        Optional<GroupEntity> groupOpt = groupRepository.findById(id);
        if (groupOpt.isEmpty()) {
            throw new SlotNotFoundException("Group not found.");
        }
        GroupEntity group = groupOpt.get();

        JoinSlotRequest req = new JoinSlotRequest();
        req.setDestinationId(group.getDestinationId());
        req.setDate(group.getDate());
        req.setTime(group.getDepartureTime());
        req.setVehicle(group.getTransport());
        req.setDirection(group.getDirection());
        req.setPickupPoint(group.getPickupPoint());
        return slotEngineService.joinSlot(req, user);
    }

    @Transactional
    public Map<String, Object> leaveGroup(String id, UserEntity user) {
        if (id.startsWith("cr_")) {
            Optional<CityRideEntity> rideOpt = cityRideRepository.findById(id);
            if (rideOpt.isEmpty()) {
                throw new SlotNotFoundException("Group not found.");
            }
            CityRideEntity ride = rideOpt.get();

            List<GroupMemberEntity> members = groupMemberRepository.findByTargetIdAndGroupTypeOrderByJoinedAtAsc(ride.getId(), "CITY_RIDE");
            boolean isMember = members.stream().anyMatch(m -> user.getId().equals(m.getUserId()));
            if (!isMember) {
                throw new IllegalArgumentException("You are not a member of this travel group.");
            }

            groupMemberRepository.deleteByTargetIdAndUserId(ride.getId(), user.getId());
            members.removeIf(m -> user.getId().equals(m.getUserId()) || user.getId().equals(m.getAddedBy()));

            if (members.isEmpty()) {
                cityRideRepository.delete(ride);
                chatMessageRepository.deleteByGroupId(ride.getId());
                Map<String, Object> res = new LinkedHashMap<>();
                res.put("message", "Ride vacated and removed.");
                res.put("removed", true);
                return res;
            } else {
                if (user.getId().equals(ride.getCreatorId())) {
                    GroupMemberEntity nextAdmin = members.get(0);
                    ride.setCreatorId(nextAdmin.getUserId());
                    ride.setCreatorName(nextAdmin.getName());
                    nextAdmin.setRole("Admin");
                    groupMemberRepository.save(nextAdmin);
                }
                if ("Full".equalsIgnoreCase(ride.getStatus()) && members.size() < ride.getMaxCapacity()) {
                    ride.setStatus("Open");
                }
                ride.setCurrentMembersCount(members.size());
                cityRideRepository.save(ride);

                Map<String, Object> res = new LinkedHashMap<>();
                res.put("message", "Successfully left the ride.");
                return res;
            }
        }

        Optional<GroupEntity> groupOpt = groupRepository.findById(id);
        if (groupOpt.isEmpty()) {
            throw new SlotNotFoundException("Group not found.");
        }

        GroupEntity group = groupOpt.get();
        List<GroupMemberEntity> members = groupMemberRepository.findByTargetIdAndGroupTypeOrderByJoinedAtAsc(group.getId(), "CAMPUS_GROUP");
        boolean isMember = members.stream().anyMatch(m -> user.getId().equals(m.getUserId()));
        if (!isMember) {
            throw new IllegalArgumentException("You are not a member of this travel group.");
        }

        double hoursLeft = SlotEngineService.getHoursUntilDeparture(group.getDate(), group.getDepartureTime(), LocalDateTime.now(IST_ZONE));
        double penalty = 0.0;
        if (hoursLeft >= 0.0 && hoursLeft <= 0.5) {
            penalty = 0.8;
        } else if (hoursLeft >= 0.0 && hoursLeft <= 4.0) {
            penalty = 0.3;
        }

        double newReliability = user.getReliability() != null ? user.getReliability() : 5.0;
        if (penalty > 0.0) {
            newReliability = Math.max(1.0, Math.round((newReliability - penalty) * 10.0) / 10.0);
            user.setReliability(newReliability);
            userRepository.save(user);
        }

        groupMemberRepository.deleteByTargetIdAndUserId(group.getId(), user.getId());
        members.removeIf(m -> user.getId().equals(m.getUserId()) || user.getId().equals(m.getAddedBy()));

        if (members.isEmpty()) {
            groupRepository.delete(group);
            chatMessageRepository.deleteByGroupId(group.getId());
        } else {
            if (user.getId().equals(group.getAdminId())) {
                GroupMemberEntity nextAdmin = members.get(0);
                group.setAdminId(nextAdmin.getUserId());
                nextAdmin.setRole("Group Admin");
                groupMemberRepository.save(nextAdmin);
                groupRepository.save(group);
            }

            ChatMessageEntity alert = new ChatMessageEntity();
            alert.setId(UUID.randomUUID().toString());
            alert.setGroupId(group.getId());
            alert.setSenderId("system");
            alert.setSenderName("Policy Alert");
            alert.setSenderAvatar("PA");
            alert.setText("⚠️ Notice: " + user.getName() + " left Group #" + group.getId() + "." + (penalty > 0 ? " (Departure plans changed)" : ""));
            chatMessageRepository.save(alert);
        }

        user.setActiveGroupId(null);
        userRepository.save(user);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", true);
        result.put("penaltyApplied", penalty > 0.0);
        result.put("penaltyAmount", penalty);
        result.put("newReliability", newReliability);
        result.put("hoursLeft", Math.round(hoursLeft * 100.0) / 100.0);
        return result;
    }

    @Transactional
    public Map<String, Object> confirmGroup(String id, UserEntity user) {
        if (id.startsWith("cr_")) {
            Optional<CityRideEntity> rideOpt = cityRideRepository.findById(id);
            if (rideOpt.isEmpty()) throw new SlotNotFoundException("Group not found.");
            CityRideEntity ride = rideOpt.get();
            if (!user.getId().equals(ride.getCreatorId())) {
                throw new UnauthorizedAccessException("Forbidden: Only the group admin can confirm this journey.", 403);
            }
            ride.setStatus("Confirmed");
            ride.setConfirmedAt(Instant.now());
            ride.setConfirmedBy(user.getId());
            cityRideRepository.save(ride);

            List<GroupMemberEntity> members = groupMemberRepository.findByTargetIdAndGroupTypeOrderByJoinedAtAsc(ride.getId(), "CITY_RIDE");
            Map<String, Object> res = new LinkedHashMap<>();
            res.put("success", true);
            res.put("group", serializerService.serializeCityRideAsGroup(ride, members, user.getId()));
            return res;
        }

        Optional<GroupEntity> groupOpt = groupRepository.findById(id);
        if (groupOpt.isEmpty()) throw new SlotNotFoundException("Group not found.");
        GroupEntity group = groupOpt.get();

        if (!user.getId().equals(group.getAdminId())) {
            throw new UnauthorizedAccessException("Forbidden: Only the group admin can confirm this journey.", 403);
        }

        group.setStatus("Confirmed");
        group.setConfirmedAt(Instant.now());
        group.setConfirmedBy(user.getId());
        groupRepository.save(group);

        List<GroupMemberEntity> members = groupMemberRepository.findByTargetIdAndGroupTypeOrderByJoinedAtAsc(group.getId(), "CAMPUS_GROUP");
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("success", true);
        res.put("group", serializerService.serializeGroup(group, members, user.getId()));
        return res;
    }

    @Transactional
    public Map<String, Object> completeGroup(String id, UserEntity user) {
        Optional<GroupEntity> groupOpt = groupRepository.findById(id);
        if (groupOpt.isEmpty()) throw new SlotNotFoundException("Group not found.");
        GroupEntity group = groupOpt.get();

        if (!user.getId().equals(group.getAdminId())) {
            throw new UnauthorizedAccessException("Forbidden: Only the group admin can mark this ride as completed.", 403);
        }

        if ("Completed".equalsIgnoreCase(group.getStatus())) {
            throw new IllegalArgumentException("This group has already been marked as completed.");
        }

        if (!"Confirmed".equalsIgnoreCase(group.getStatus())) {
            throw new IllegalArgumentException("Only Confirmed groups can be marked as completed.");
        }

        group.setStatus("Completed");
        group.setCompletedAt(Instant.now());
        group.setCompletedBy(user.getId());
        groupRepository.save(group);

        List<GroupMemberEntity> members = groupMemberRepository.findByTargetIdAndGroupTypeOrderByJoinedAtAsc(group.getId(), "CAMPUS_GROUP");

        // Immutable auditable ride completion record
        RideRecordEntity record = new RideRecordEntity();
        record.setId(UUID.randomUUID().toString());
        record.setGroupId(group.getId());
        record.setDestinationId(group.getDestinationId());
        record.setDestinationName(group.getDestinationName());
        record.setDepartureTime(group.getDepartureTime());
        record.setDate(group.getDate());
        record.setTransport(group.getTransport());
        record.setPickupPoint(group.getPickupPoint());
        record.setAdminId(group.getAdminId());
        record.setConfirmedAt(group.getConfirmedAt() != null ? group.getConfirmedAt() : Instant.now());
        record.setCompletedAt(Instant.now());
        record.setCompletedBy(user.getId());
        rideRecordRepository.save(record);

        // Increment tripsCompleted for all members
        for (GroupMemberEntity m : members) {
            Optional<UserEntity> uOpt = userRepository.findById(m.getUserId());
            if (uOpt.isPresent()) {
                UserEntity u = uOpt.get();
                u.setTripsCompleted(u.getTripsCompleted() + 1);
                u.setActiveGroupId(null);
                userRepository.save(u);
            }
        }

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("success", true);
        res.put("group", serializerService.serializeGroup(group, members, user.getId()));
        return res;
    }

    @Transactional
    public Map<String, Object> changePickupPoint(String id, ChangePickupRequest req, UserEntity user) {
        if (req.getPickupPoint() == null || !req.getPickupPoint().toLowerCase().contains("main gate")) {
            throw new IllegalArgumentException("Campus transit pickup point must be Main Gate.");
        }

        Optional<GroupEntity> groupOpt = groupRepository.findById(id);
        if (groupOpt.isEmpty()) throw new SlotNotFoundException("Group not found.");
        GroupEntity group = groupOpt.get();

        group.setPickupPoint("Main Gate");
        groupRepository.save(group);

        List<GroupMemberEntity> members = groupMemberRepository.findByTargetIdAndGroupTypeOrderByJoinedAtAsc(group.getId(), "CAMPUS_GROUP");
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("success", true);
        res.put("group", serializerService.serializeGroup(group, members, user.getId()));
        return res;
    }

    @Transactional
    public Map<String, Object> addFriend(String id, AddFriendRequest req, UserEntity user) {
        if (req.getName() == null || req.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Companion name is required.");
        }

        Optional<GroupEntity> groupOpt = groupRepository.findById(id);
        if (groupOpt.isEmpty()) throw new SlotNotFoundException("Group not found.");
        GroupEntity group = groupOpt.get();

        List<GroupMemberEntity> members = groupMemberRepository.findByTargetIdAndGroupTypeOrderByJoinedAtAsc(group.getId(), "CAMPUS_GROUP");
        if (members.size() >= group.getMaxMembers()) {
            throw new OverbookingException("Group has reached its maximum vehicle capacity (4 seats).");
        }

        GroupMemberEntity companion = new GroupMemberEntity();
        companion.setId(UUID.randomUUID().toString());
        companion.setTargetId(group.getId());
        companion.setGroupType("CAMPUS_GROUP");
        companion.setUserId("companion_" + UUID.randomUUID().toString().substring(0, 8));
        companion.setName(req.getName().trim() + " (Friend of " + user.getName().split(" ")[0] + ")");
        companion.setAvatar(user.getAvatar());
        companion.setHostel(req.getHostel() != null ? req.getHostel() : user.getHostel());
        companion.setPhone(req.getPhone() != null ? req.getPhone() : user.getPhone());
        companion.setRole("Companion");
        companion.setIsCompanion(true);
        companion.setAddedBy(user.getId());
        companion.setCompanionName(req.getName().trim());
        companion.setDepartureTime(group.getDepartureTime());
        companion.setReliability(user.getReliability());
        companion.setJoinedAt(Instant.now());
        groupMemberRepository.save(companion);

        members.add(companion);

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("success", true);
        res.put("group", serializerService.serializeGroup(group, members, user.getId()));
        return res;
    }

    @Transactional
    public Map<String, Object> removeFriend(String id, String friendId, UserEntity user) {
        Optional<GroupEntity> groupOpt = groupRepository.findById(id);
        if (groupOpt.isEmpty()) throw new SlotNotFoundException("Group not found.");
        GroupEntity group = groupOpt.get();

        Optional<GroupMemberEntity> friendOpt = groupMemberRepository.findById(friendId);
        if (friendOpt.isPresent()) {
            GroupMemberEntity mem = friendOpt.get();
            if (user.getId().equals(mem.getAddedBy())) {
                groupMemberRepository.delete(mem);
            }
        }

        List<GroupMemberEntity> members = groupMemberRepository.findByTargetIdAndGroupTypeOrderByJoinedAtAsc(group.getId(), "CAMPUS_GROUP");
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("success", true);
        res.put("group", serializerService.serializeGroup(group, members, user.getId()));
        return res;
    }

    public List<Map<String, Object>> getMessages(String id, UserEntity user) {
        List<ChatMessageEntity> messages = chatMessageRepository.findByGroupIdOrderByCreatedAtAsc(id);
        List<Map<String, Object>> list = new ArrayList<>();
        for (ChatMessageEntity m : messages) {
            list.add(serializerService.serializeChatMessage(m));
        }
        return list;
    }

    @Transactional
    public Map<String, Object> sendMessage(String id, SendMessageRequest req, UserEntity user) {
        if (req.getText() == null || req.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Message text cannot be empty.");
        }

        ChatMessageEntity msg = new ChatMessageEntity();
        msg.setId(UUID.randomUUID().toString());
        msg.setGroupId(id);
        msg.setSenderId(user.getId());
        msg.setSenderName(user.getName());
        msg.setSenderAvatar(user.getAvatar());
        msg.setSenderHostel(user.getHostel());
        msg.setText(req.getText().trim());
        msg.setCreatedAt(Instant.now());
        chatMessageRepository.save(msg);

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("success", true);
        res.put("message", serializerService.serializeChatMessage(msg));
        return res;
    }
}
