package com.tiet.journeyconnect.services;

import com.tiet.journeyconnect.dto.CommunityDtos.*;
import com.tiet.journeyconnect.dto.TravelDtos.*;
import com.tiet.journeyconnect.exceptions.OverbookingException;
import com.tiet.journeyconnect.exceptions.SingleCommunityRuleViolationException;
import com.tiet.journeyconnect.exceptions.SingleRidePolicyViolationException;
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
public class CommunityService {

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private CommunityPostRepository communityPostRepository;

    @Autowired
    private PostCommentRepository postCommentRepository;

    @Autowired
    private CityRideRepository cityRideRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SerializerService serializerService;

    public List<Map<String, Object>> getCommunities() {
        List<CommunityEntity> list = communityRepository.findAllByOrderByMembersCountDescNameAsc();
        List<Map<String, Object>> result = new ArrayList<>();
        for (CommunityEntity c : list) {
            result.add(serializerService.serializeCommunity(c));
        }
        return result;
    }

    @Transactional
    public Map<String, Object> createCommunity(CreateCommunityRequest req, UserEntity user) {
        if (req.getName() == null || req.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Community / City name is required.");
        }
        if (req.getState() == null || req.getState().trim().isEmpty()) {
            throw new IllegalArgumentException("State / Province is required.");
        }

        String cleanName = req.getName().trim();
        String cleanState = req.getState().trim();
        String idSlug = (cleanState + "-" + cleanName).toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
        if (idSlug.isEmpty()) {
            idSlug = "comm_" + (System.currentTimeMillis() % 10000);
        }

        Optional<CommunityEntity> existing = communityRepository.findById(idSlug);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("A hometown hub for \"" + cleanName + "\" already exists.");
        }

        CommunityEntity comm = new CommunityEntity();
        comm.setId(idSlug);
        comm.setName(cleanName);
        comm.setState(cleanState);
        comm.setIcon(req.getIcon() != null ? req.getIcon() : "🏙️");
        comm.setMembersCount(0);
        comm.setDistanceFromThapar(req.getDistanceFromThapar() != null ? req.getDistanceFromThapar() : "250 km");
        comm.setPopularTransport(req.getPopularTransport() != null ? req.getPopularTransport() : "Trains, Shared Cabs, Express Buses");
        comm.setDescription(req.getDescription() != null ? req.getDescription() : "Official student community for Thapar students hailing from " + cleanName + ".");
        if (req.getTags() != null) {
            comm.setTags(String.join(",", req.getTags()));
        }
        comm.setCreatedBy(user.getId());
        comm.setCreatedAt(Instant.now());
        comm = communityRepository.save(comm);

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("success", true);
        res.put("community", serializerService.serializeCommunity(comm));
        return res;
    }

    @Transactional
    public Map<String, Object> joinCommunity(String communityId, UserEntity user) {
        Optional<CommunityEntity> commOpt = communityRepository.findById(communityId);
        if (commOpt.isEmpty()) {
            throw new IllegalArgumentException("Community hub not found.");
        }
        CommunityEntity comm = commOpt.get();

        if (user.getJoinedCommunity() != null && !user.getJoinedCommunity().equals(communityId)) {
            Optional<CommunityEntity> current = communityRepository.findById(user.getJoinedCommunity());
            String curName = current.map(CommunityEntity::getName).orElse("another");
            throw new SingleCommunityRuleViolationException(curName, comm.getName());
        }

        if (!communityId.equals(user.getJoinedCommunity())) {
            user.setJoinedCommunity(communityId);
            user.setHometown(comm.getName());
            userRepository.save(user);

            long count = userRepository.countByJoinedCommunity(communityId);
            comm.setMembersCount((int) count);
            communityRepository.save(comm);
        }

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("success", true);
        res.put("message", "Enrolled into " + comm.getName() + " Community Hub!");
        res.put("community", serializerService.serializeCommunity(comm));
        return res;
    }

    @Transactional
    public Map<String, Object> leaveCommunity(String communityId, UserEntity user) {
        if (communityId.equals(user.getJoinedCommunity())) {
            user.setJoinedCommunity(null);
            userRepository.save(user);

            Optional<CommunityEntity> commOpt = communityRepository.findById(communityId);
            commOpt.ifPresent(comm -> {
                long count = userRepository.countByJoinedCommunity(communityId);
                comm.setMembersCount((int) count);
                communityRepository.save(comm);
            });
        }

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("success", true);
        res.put("message", "Left community hub.");
        return res;
    }

    public List<Map<String, Object>> getCommunityPosts(String communityId, UserEntity user) {
        List<CommunityPostEntity> posts = communityPostRepository.findByCommunityIdOrderByCreatedAtDesc(communityId);
        List<Map<String, Object>> list = new ArrayList<>();
        for (CommunityPostEntity p : posts) {
            List<PostCommentEntity> comments = postCommentRepository.findByPostIdOrderByCreatedAtAsc(p.getId());
            list.add(serializerService.serializePost(p, comments, user.getId()));
        }
        return list;
    }

    @Transactional
    public Map<String, Object> createCommunityPost(String communityId, CreatePostRequest req, UserEntity user) {
        if (!communityId.equals(user.getJoinedCommunity())) {
            throw new UnauthorizedAccessException("Forbidden: Only enrolled members of this community can create notice board posts.", 403);
        }

        if (req.getTitle() == null || req.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Post title is required.");
        }
        if (req.getContent() == null || req.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Post content is required.");
        }

        CommunityPostEntity post = new CommunityPostEntity();
        post.setId(UUID.randomUUID().toString());
        post.setCommunityId(communityId);
        post.setAuthorId(user.getId());
        post.setAuthorName(user.getName());
        post.setAuthorAvatar(user.getAvatar());
        post.setAuthorHostel(user.getHostel());
        post.setCategory(req.getCategory() != null ? req.getCategory() : "Ride Sharing");
        post.setTitle(req.getTitle().trim());
        post.setContent(req.getContent().trim());
        post.setCreatedAt(Instant.now());
        post = communityPostRepository.save(post);

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("success", true);
        res.put("post", serializerService.serializePost(post, Collections.emptyList(), user.getId()));
        return res;
    }

    @Transactional
    public Map<String, Object> toggleLikePost(String postId, UserEntity user) {
        Optional<CommunityPostEntity> postOpt = communityPostRepository.findById(postId);
        if (postOpt.isEmpty()) {
            throw new IllegalArgumentException("Post not found.");
        }
        CommunityPostEntity post = postOpt.get();

        Set<String> likes = new HashSet<>();
        if (post.getLikes() != null && !post.getLikes().trim().isEmpty()) {
            likes.addAll(Arrays.asList(post.getLikes().split(",")));
        }

        boolean nowLiked;
        if (likes.contains(user.getId())) {
            likes.remove(user.getId());
            nowLiked = false;
        } else {
            likes.add(user.getId());
            nowLiked = true;
        }

        post.setLikes(String.join(",", likes));
        communityPostRepository.save(post);

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("success", true);
        res.put("liked", nowLiked);
        res.put("likesCount", likes.size());
        return res;
    }

    @Transactional
    public Map<String, Object> addComment(String postId, AddCommentRequest req, UserEntity user) {
        if (req.getText() == null || req.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Comment text cannot be empty.");
        }

        Optional<CommunityPostEntity> postOpt = communityPostRepository.findById(postId);
        if (postOpt.isEmpty()) {
            throw new IllegalArgumentException("Post not found.");
        }

        PostCommentEntity comment = new PostCommentEntity();
        comment.setId(UUID.randomUUID().toString());
        comment.setPostId(postId);
        comment.setAuthorId(user.getId());
        comment.setAuthorName(user.getName());
        comment.setAuthorAvatar(user.getAvatar());
        comment.setAuthorHostel(user.getHostel());
        comment.setText(req.getText().trim());
        comment.setCreatedAt(Instant.now());
        comment = postCommentRepository.save(comment);

        Map<String, Object> commentMap = new LinkedHashMap<>();
        commentMap.put("id", comment.getId());
        commentMap.put("postId", comment.getPostId());
        commentMap.put("authorId", comment.getAuthorId());
        commentMap.put("author", comment.getAuthorName());
        commentMap.put("authorAvatar", comment.getAuthorAvatar());
        commentMap.put("authorHostel", comment.getAuthorHostel());
        commentMap.put("text", comment.getText());
        commentMap.put("createdAt", comment.getCreatedAt().toString());

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("success", true);
        res.put("comment", commentMap);
        return res;
    }

    public List<Map<String, Object>> getCityRides(String communityId, String mode, String date, UserEntity user) {
        List<CityRideEntity> rides;
        if (date != null && !date.isEmpty() && !"All".equalsIgnoreCase(date)) {
            rides = cityRideRepository.findByCommunityIdAndDateAndStatusIn(communityId, date, List.of("Open", "Full", "Confirmed"));
        } else {
            rides = cityRideRepository.findByCommunityIdAndStatusIn(communityId, List.of("Open", "Full", "Confirmed"));
        }

        List<Map<String, Object>> list = new ArrayList<>();
        for (CityRideEntity r : rides) {
            if (mode == null || mode.isEmpty() || "All".equalsIgnoreCase(mode) || r.getMode().equalsIgnoreCase(mode)) {
                List<GroupMemberEntity> members = groupMemberRepository.findByTargetIdAndGroupTypeOrderByJoinedAtAsc(r.getId(), "CITY_RIDE");
                list.add(serializerService.serializeCityRide(r, members, user.getId()));
            }
        }
        return list;
    }

    @Transactional
    public Map<String, Object> createCityRide(String communityId, CreateCityRideRequest req, UserEntity user) {
        if (user.getJoinedCommunity() == null || user.getJoinedCommunity().trim().isEmpty()) {
            user.setJoinedCommunity(communityId);
            userRepository.save(user);
        }

        if (req.getDestination() == null || req.getDate() == null || req.getTime() == null) {
            throw new IllegalArgumentException("Destination, date, and departure time are required.");
        }

        Optional<CommunityEntity> commOpt = communityRepository.findById(communityId);
        String commName = commOpt.map(CommunityEntity::getName).orElse(communityId);

        String rideId = "cr_" + UUID.randomUUID().toString().substring(0, 8);
        String selectedMode = (req.getMode() != null && "Train".equalsIgnoreCase(req.getMode())) ? "Train" : "Cab";
        int maxCap = (req.getMaxCapacity() != null && req.getMaxCapacity() > 0) ? req.getMaxCapacity() : ("Train".equals(selectedMode) ? 8 : 4);

        CityRideEntity ride = new CityRideEntity();
        ride.setId(rideId);
        ride.setCommunityId(communityId);
        ride.setCommunityName(commName);
        ride.setCreatorId(user.getId());
        ride.setCreatorName(user.getName());
        ride.setDestination(req.getDestination().trim());
        ride.setDate(SlotEngineService.normalizeDate(req.getDate()));
        ride.setTime(req.getTime().trim());
        ride.setMode(selectedMode);
        ride.setPickupPoint("Main Gate");
        ride.setTotalFare(req.getTotalFare() != null ? req.getTotalFare() : 0.0);
        ride.setMaxCapacity(maxCap);
        ride.setCurrentMembersCount(1);
        ride.setTransitDetails(req.getTransitDetails() != null ? req.getTransitDetails() : "");
        ride.setNotes(req.getNotes() != null ? req.getNotes() : "");
        ride.setDirection(req.getDirection() != null ? req.getDirection() : "departing");
        ride.setStatus("Open");
        ride.setCreatedAt(Instant.now());
        ride = cityRideRepository.save(ride);

        GroupMemberEntity creatorMem = new GroupMemberEntity();
        creatorMem.setId(UUID.randomUUID().toString());
        creatorMem.setTargetId(ride.getId());
        creatorMem.setGroupType("CITY_RIDE");
        creatorMem.setUserId(user.getId());
        creatorMem.setName(user.getName());
        creatorMem.setAvatar(user.getAvatar());
        creatorMem.setHostel(user.getHostel());
        creatorMem.setReliability(user.getReliability());
        creatorMem.setPhone(user.getPhone());
        creatorMem.setRole("Admin");
        creatorMem.setJoinedAt(Instant.now());
        groupMemberRepository.save(creatorMem);

        user.setActiveGroupId(ride.getId());
        userRepository.save(user);

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("success", true);
        res.put("ride", serializerService.serializeCityRide(ride, List.of(creatorMem), user.getId()));
        return res;
    }

    public List<Map<String, Object>> getCityCommunitySlots(String communityId, String destination, String date, String mode, String direction, UserEntity user) {
        String travelDate = SlotEngineService.normalizeDate(date);
        Optional<CommunityEntity> commOpt = communityRepository.findById(communityId);
        String commName = commOpt.map(CommunityEntity::getName).orElse(communityId);
        String targetDest = (destination != null && !destination.isEmpty()) ? destination : commName;
        String dir = (direction != null && !direction.isEmpty()) ? direction : "departing";

        List<CityRideEntity> rides = cityRideRepository.findByCommunityIdAndDateAndStatusIn(communityId, travelDate, List.of("Open", "Full", "Confirmed"));
        Map<String, CityRideEntity> ridesByTime = new HashMap<>();
        for (CityRideEntity r : rides) {
            if (dir.equalsIgnoreCase(r.getDirection())) {
                ridesByTime.put(r.getTime(), r);
            }
        }

        String originName = "departing".equalsIgnoreCase(dir) ? "Thapar Campus (Main Gate)" : targetDest;
        String destName = "departing".equalsIgnoreCase(dir) ? targetDest : "Thapar Campus (Main Gate)";
        LocalDateTime nowIST = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));

        List<Map<String, Object>> slots = new ArrayList<>();
        for (int h = 0; h < 24; h++) {
            for (int m = 0; m < 60; m += 15) {
                String timeStr = String.format("%02d:%02d", h, m);
                CityRideEntity r = ridesByTime.get(timeStr);

                double hoursLeft = SlotEngineService.getHoursUntilDeparture(travelDate, timeStr, nowIST);
                boolean isPast = hoursLeft < (-5.0 / 60.0);

                boolean isJoined = false;
                if (r != null && user != null) {
                    isJoined = groupMemberRepository.findByTargetIdAndGroupTypeOrderByJoinedAtAsc(r.getId(), "CITY_RIDE")
                            .stream().anyMatch(mem -> user.getId().equals(mem.getUserId()));
                }

                Map<String, Object> s = new LinkedHashMap<>();
                s.put("slotKey", "comm_" + communityId + "_" + travelDate + "_" + timeStr + "_" + dir);
                s.put("time", timeStr);
                s.put("date", travelDate);
                s.put("origin", originName);
                s.put("destination", destName);
                s.put("direction", dir);
                s.put("mode", mode != null ? mode : "Cab");
                s.put("status", r != null ? r.getStatus() : "Open");
                s.put("seatsFilled", r != null ? r.getCurrentMembersCount() : 0);
                s.put("maxSeats", r != null ? r.getMaxCapacity() : 4);
                s.put("isFull", r != null && r.getCurrentMembersCount() >= r.getMaxCapacity());
                s.put("isJoined", isJoined);
                s.put("isPast", isPast);
                s.put("rideId", r != null ? r.getId() : null);
                slots.add(s);
            }
        }
        return slots;
    }

    @Transactional
    public Map<String, Object> joinCityCommunitySlot(String communityId, JoinCityCommunitySlotRequest req, UserEntity user) {
        if (user.getJoinedCommunity() == null || user.getJoinedCommunity().trim().isEmpty()) {
            user.setJoinedCommunity(communityId);
            userRepository.save(user);
        }

        String travelDate = SlotEngineService.normalizeDate(req.getDate());
        String travelTime = req.getTime() != null ? req.getTime().trim() : "12:00";
        String tripDirection = (req.getDirection() != null && !req.getDirection().trim().isEmpty()) ? req.getDirection().trim() : "departing";
        String selectedMode = (req.getMode() != null && !req.getMode().trim().isEmpty()) ? req.getMode().trim() : "Cab";

        // Check if an existing open ride already exists for this community, date, time, and direction
        List<CityRideEntity> existingRides = cityRideRepository.findByCommunityIdAndDateAndStatusIn(
                communityId, travelDate, List.of("Open", "Confirmed")
        );
        CityRideEntity targetRide = null;
        for (CityRideEntity r : existingRides) {
            if (travelTime.equals(r.getTime()) && tripDirection.equalsIgnoreCase(r.getDirection())) {
                targetRide = r;
                break;
            }
        }

        if (targetRide != null) {
            List<GroupMemberEntity> currentMembers = groupMemberRepository.findByTargetIdAndGroupTypeOrderByJoinedAtAsc(targetRide.getId(), "CITY_RIDE");
            boolean alreadyMember = currentMembers.stream().anyMatch(m -> user.getId().equals(m.getUserId()));
            if (!alreadyMember) {
                if (currentMembers.size() >= targetRide.getMaxCapacity()) {
                    throw new OverbookingException("This cab ride is already full.");
                }
                GroupMemberEntity mem = new GroupMemberEntity();
                mem.setId(UUID.randomUUID().toString());
                mem.setTargetId(targetRide.getId());
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

                currentMembers.add(mem);
                targetRide.setCurrentMembersCount(currentMembers.size());
                if (currentMembers.size() >= targetRide.getMaxCapacity()) {
                    targetRide.setStatus("Full");
                }
                cityRideRepository.save(targetRide);
            }

            user.setActiveGroupId(targetRide.getId());
            userRepository.save(user);

            Map<String, Object> res = new LinkedHashMap<>();
            res.put("success", true);
            res.put("ride", serializerService.serializeCityRide(targetRide, currentMembers, user.getId()));
            return res;
        } else {
            CreateCityRideRequest rideReq = new CreateCityRideRequest();
            rideReq.setDestination(req.getDestination());
            rideReq.setDate(travelDate);
            rideReq.setTime(travelTime);
            rideReq.setMode(selectedMode);
            rideReq.setDirection(tripDirection);
            return createCityRide(communityId, rideReq, user);
        }
    }
}
