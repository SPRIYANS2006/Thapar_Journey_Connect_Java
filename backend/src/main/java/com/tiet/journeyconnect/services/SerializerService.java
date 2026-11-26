package com.tiet.journeyconnect.services;

import com.tiet.journeyconnect.models.entities.*;
import com.tiet.journeyconnect.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Serializer Service responsible for converting database JPA Entities into
 * privacy-sanitized, camelCase DTOs matching the exact React 19 API contracts.
 */
@Service
public class SerializerService {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_INSTANT;

    @Autowired
    private UserRepository userRepository;

    public Map<String, Object> serializeUser(UserEntity user) {
        if (user == null) return null;
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", user.getId());
        map.put("_id", user.getId());
        map.put("name", user.getName());
        map.put("email", user.getEmail());
        map.put("hostel", user.getHostel());
        map.put("avatar", user.getAvatar());
        map.put("phone", user.getPhone());
        map.put("degree", user.getDegree());
        map.put("year", user.getYear());
        map.put("hometown", user.getHometown());
        map.put("rollNumber", user.getRollNumber());
        map.put("preferredTransport", user.getPreferredTransport());
        map.put("timeWindow", user.getTimeWindow());

        List<String> routes = new ArrayList<>();
        if (user.getFrequentRoutes() != null && !user.getFrequentRoutes().trim().isEmpty()) {
            routes = Arrays.asList(user.getFrequentRoutes().split(","));
        }
        map.put("frequentRoutes", routes);
        map.put("rideNotifications", user.getRideNotifications());

        List<String> muted = new ArrayList<>();
        if (user.getMutedUsers() != null && !user.getMutedUsers().trim().isEmpty()) {
            muted = Arrays.asList(user.getMutedUsers().split(","));
        }
        map.put("mutedUsers", muted);
        map.put("reliability", Math.round(user.getReliability() * 10.0) / 10.0);
        map.put("tripsCompleted", user.getTripsCompleted());
        map.put("role", user.getRole());
        map.put("joinedCommunity", user.getJoinedCommunity());
        map.put("hasPassword", user.getPasswordHash() != null && !user.getPasswordHash().isEmpty());
        return map;
    }

    public Map<String, Object> serializeMember(GroupMemberEntity m, boolean canSeePhone) {
        if (m == null) return null;
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", m.getId());
        map.put("_id", m.getId());
        map.put("userId", m.getUserId());

        String memberName = m.getName();
        String memberAvatar = m.getAvatar();
        if (m.getUserId() != null && userRepository != null) {
            Optional<UserEntity> uOpt = userRepository.findById(m.getUserId());
            if (uOpt.isPresent()) {
                UserEntity u = uOpt.get();
                if (u.getName() != null && !u.getName().isEmpty()) {
                    memberName = u.getName();
                }
                if (u.getAvatar() != null && !u.getAvatar().isEmpty()) {
                    memberAvatar = u.getAvatar();
                }
            }
        }

        map.put("name", memberName);
        map.put("avatar", memberAvatar);
        map.put("hostel", m.getHostel());
        map.put("reliability", m.getReliability());
        map.put("departureTime", m.getDepartureTime());
        map.put("role", m.getRole());
        map.put("phone", canSeePhone ? m.getPhone() : "");
        map.put("isCompanion", Boolean.TRUE.equals(m.getIsCompanion()));
        map.put("addedBy", m.getAddedBy());
        map.put("companionName", m.getCompanionName());
        map.put("joinedAt", m.getJoinedAt() != null ? m.getJoinedAt().toString() : "");
        return map;
    }

    public Map<String, Object> serializeGroup(GroupEntity group, List<GroupMemberEntity> members, String viewingUserId) {
        if (group == null) return null;
        boolean isMember = viewingUserId != null && members.stream().anyMatch(m -> viewingUserId.equals(m.getUserId()));
        boolean isConfirmed = "Confirmed".equalsIgnoreCase(group.getStatus()) || "Completed".equalsIgnoreCase(group.getStatus());
        boolean canSeePhone = isMember && isConfirmed;

        List<Map<String, Object>> serializedMembers = new ArrayList<>();
        for (GroupMemberEntity m : members) {
            serializedMembers.add(serializeMember(m, canSeePhone));
        }

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", group.getId());
        map.put("_id", group.getId());
        map.put("destinationId", group.getDestinationId());
        map.put("destinationName", group.getDestinationName());
        map.put("departureTime", group.getDepartureTime());
        map.put("date", group.getDate());
        map.put("status", group.getStatus());
        map.put("transport", group.getTransport());
        map.put("origin", group.getOrigin());
        map.put("direction", group.getDirection());
        map.put("maxMembers", group.getMaxMembers());
        map.put("pickupPoint", "Main Gate");
        map.put("adminFare", "Shared Auto".equalsIgnoreCase(group.getTransport()) ? null : group.getAdminFare());
        map.put("slotKey", group.getSlotKey());
        map.put("matchScore", group.getMatchScore());
        map.put("adminId", group.getAdminId());
        map.put("members", serializedMembers);
        map.put("confirmedAt", group.getConfirmedAt() != null ? group.getConfirmedAt().toString() : null);
        map.put("completionVerified", group.getCompletedBy() != null && !"system_scheduler".equals(group.getCompletedBy()));
        map.put("completedAt", group.getCompletedAt() != null ? group.getCompletedAt().toString() : null);
        map.put("createdAt", group.getCreatedAt() != null ? group.getCreatedAt().toString() : null);
        return map;
    }

    public Map<String, Object> serializeCityRide(CityRideEntity ride, List<GroupMemberEntity> members, String viewingUserId) {
        if (ride == null) return null;
        boolean isMember = viewingUserId != null && members.stream().anyMatch(m -> viewingUserId.equals(m.getUserId()));
        boolean isConfirmed = "Confirmed".equalsIgnoreCase(ride.getStatus()) || "Completed".equalsIgnoreCase(ride.getStatus());
        boolean canSeePhone = isMember && isConfirmed;

        int count = Math.max(1, members.size());
        double farePerPerson = ("Cab".equalsIgnoreCase(ride.getMode()) && ride.getTotalFare() > 0 && count > 0)
                ? Math.round(ride.getTotalFare() / count)
                : (ride.getTotalFare() != null ? ride.getTotalFare() : 0.0);

        int seatsLeft = Math.max(0, ride.getMaxCapacity() - count);

        List<Map<String, Object>> serializedMembers = new ArrayList<>();
        for (GroupMemberEntity m : members) {
            serializedMembers.add(serializeMember(m, canSeePhone));
        }

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", ride.getId());
        map.put("_id", ride.getId());
        map.put("communityId", ride.getCommunityId());
        map.put("communityName", ride.getCommunityName());
        map.put("creatorId", ride.getCreatorId());
        map.put("creatorName", ride.getCreatorName());
        map.put("origin", ride.getOrigin());
        map.put("destination", ride.getDestination());
        map.put("direction", ride.getDirection());
        map.put("date", ride.getDate());
        map.put("time", ride.getTime());
        map.put("mode", ride.getMode());
        map.put("pickupPoint", "Main Gate");
        map.put("adminFare", "Cab".equalsIgnoreCase(ride.getMode()) ? ride.getAdminFare() : null);
        map.put("totalFare", ride.getTotalFare());
        map.put("farePerPerson", farePerPerson);
        map.put("maxCapacity", ride.getMaxCapacity());
        map.put("currentMembersCount", count);
        map.put("seatsLeft", seatsLeft);
        map.put("isFull", seatsLeft == 0 || "Full".equalsIgnoreCase(ride.getStatus()));
        map.put("transitDetails", ride.getTransitDetails());
        map.put("notes", ride.getNotes());
        map.put("status", ride.getStatus());
        map.put("isMember", isMember);
        map.put("members", serializedMembers);
        map.put("createdAt", ride.getCreatedAt() != null ? ride.getCreatedAt().toString() : null);
        return map;
    }

    public Map<String, Object> serializeCityRideAsGroup(CityRideEntity ride, List<GroupMemberEntity> members, String viewingUserId) {
        if (ride == null) return null;
        boolean isMember = viewingUserId != null && members.stream().anyMatch(m -> viewingUserId.equals(m.getUserId()));
        boolean isConfirmed = "Confirmed".equalsIgnoreCase(ride.getStatus()) || "Completed".equalsIgnoreCase(ride.getStatus());
        boolean canSeePhone = isMember && isConfirmed;

        int count = Math.max(1, members.size());
        double farePerPerson = ("Cab".equalsIgnoreCase(ride.getMode()) && ride.getTotalFare() > 0 && count > 0)
                ? Math.round(ride.getTotalFare() / count)
                : (ride.getTotalFare() != null ? ride.getTotalFare() : 0.0);

        List<Map<String, Object>> serializedMembers = new ArrayList<>();
        for (GroupMemberEntity m : members) {
            Map<String, Object> memMap = serializeMember(m, canSeePhone);
            if ("Admin".equalsIgnoreCase(m.getRole())) {
                memMap.put("role", "Group Admin");
            }
            serializedMembers.add(memMap);
        }

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", ride.getId());
        map.put("_id", ride.getId());
        map.put("destinationId", "comm_" + ride.getCommunityId());
        map.put("destinationName", ride.getDestination() + " (" + ride.getCommunityName() + ")");
        map.put("departureTime", ride.getTime());
        map.put("date", ride.getDate());
        map.put("status", ride.getStatus());
        map.put("transport", "Cab".equalsIgnoreCase(ride.getMode()) ? "Shared Cab" : "Train");
        map.put("origin", ride.getOrigin());
        map.put("direction", ride.getDirection());
        map.put("maxMembers", ride.getMaxCapacity());
        map.put("pickupPoint", "Main Gate");
        map.put("adminFare", "Cab".equalsIgnoreCase(ride.getMode()) ? ride.getAdminFare() : null);
        map.put("adminId", ride.getCreatorId());
        map.put("creatorId", ride.getCreatorId());
        map.put("isCommunityRide", true);
        map.put("communityId", ride.getCommunityId());
        map.put("communityName", ride.getCommunityName());
        map.put("farePerPerson", farePerPerson);
        map.put("totalFare", ride.getTotalFare());
        map.put("transitDetails", ride.getTransitDetails());
        map.put("notes", ride.getNotes());
        map.put("members", serializedMembers);
        map.put("completionVerified", ride.getCompletedBy() != null && !"system_scheduler".equals(ride.getCompletedBy()));
        map.put("createdAt", ride.getCreatedAt() != null ? ride.getCreatedAt().toString() : null);
        return map;
    }

    public Map<String, Object> serializeTravelGroup(TravelGroupEntity group, List<GroupMemberEntity> members, String viewingUserId) {
        if (group == null) return null;
        boolean isMember = viewingUserId != null && members.stream().anyMatch(m -> viewingUserId.equals(m.getUserId()));

        List<Map<String, Object>> serializedMembers = new ArrayList<>();
        for (GroupMemberEntity m : members) {
            serializedMembers.add(serializeMember(m, isMember));
        }

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", group.getId());
        map.put("_id", group.getId());
        map.put("type", group.getType());
        map.put("title", group.getTitle());
        map.put("communityId", group.getCommunityId());
        map.put("communityName", group.getCommunityName());
        map.put("creatorId", group.getCreatorId());
        map.put("creatorName", group.getCreatorName());
        map.put("date", group.getDate());
        map.put("departureTime", group.getDepartureTime());
        map.put("boardingPoint", group.getBoardingPoint());
        map.put("destination", group.getDestination());
        map.put("trainNumber", group.getTrainNumber());
        map.put("travelClass", group.getTravelClass());
        map.put("busOperator", group.getBusOperator());
        map.put("description", group.getDescription());
        map.put("membersCount", Math.max(1, members.size()));
        map.put("status", group.getStatus());
        map.put("isMember", isMember);
        map.put("members", serializedMembers);
        map.put("createdAt", group.getCreatedAt() != null ? group.getCreatedAt().toString() : null);
        return map;
    }

    public Map<String, Object> serializeCommunity(CommunityEntity comm) {
        if (comm == null) return null;
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", comm.getId());
        map.put("_id", comm.getId());
        map.put("name", comm.getName());
        map.put("state", comm.getState());
        map.put("icon", comm.getIcon());
        map.put("membersCount", comm.getMembersCount());
        map.put("distanceFromThapar", comm.getDistanceFromThapar());
        map.put("popularTransport", comm.getPopularTransport());
        map.put("description", comm.getDescription());

        List<String> tags = new ArrayList<>();
        if (comm.getTags() != null && !comm.getTags().trim().isEmpty()) {
            tags = Arrays.asList(comm.getTags().split(","));
        }
        map.put("tags", tags);
        return map;
    }

    public Map<String, Object> serializePost(CommunityPostEntity post, List<PostCommentEntity> comments, String viewingUserId) {
        if (post == null) return null;
        Set<String> likes = new HashSet<>();
        if (post.getLikes() != null && !post.getLikes().trim().isEmpty()) {
            likes.addAll(Arrays.asList(post.getLikes().split(",")));
        }

        List<Map<String, Object>> commentMaps = new ArrayList<>();
        for (PostCommentEntity c : comments) {
            Map<String, Object> cm = new LinkedHashMap<>();
            cm.put("id", c.getId());
            cm.put("_id", c.getId());
            cm.put("postId", c.getPostId());
            cm.put("authorId", c.getAuthorId());
            cm.put("author", c.getAuthorName());
            cm.put("authorAvatar", c.getAuthorAvatar());
            cm.put("authorHostel", c.getAuthorHostel());
            cm.put("text", c.getText());
            cm.put("createdAt", c.getCreatedAt() != null ? c.getCreatedAt().toString() : null);
            commentMaps.add(cm);
        }

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", post.getId());
        map.put("_id", post.getId());
        map.put("communityId", post.getCommunityId());
        map.put("authorId", post.getAuthorId());
        map.put("author", post.getAuthorName());
        map.put("authorAvatar", post.getAuthorAvatar());
        map.put("authorHostel", post.getAuthorHostel());
        map.put("category", post.getCategory());
        map.put("title", post.getTitle());
        map.put("content", post.getContent());
        map.put("likes", likes.size());
        map.put("isLiked", viewingUserId != null && likes.contains(viewingUserId));
        map.put("repliesCount", comments.size());
        map.put("comments", commentMaps);
        map.put("createdAt", post.getCreatedAt() != null ? post.getCreatedAt().toString() : null);
        return map;
    }

    public Map<String, Object> serializeChatMessage(ChatMessageEntity msg) {
        if (msg == null) return null;
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", msg.getId());
        map.put("_id", msg.getId());
        map.put("groupId", msg.getGroupId());
        map.put("senderId", msg.getSenderId());
        map.put("senderName", msg.getSenderName());
        map.put("avatar", msg.getSenderAvatar());
        map.put("senderAvatar", msg.getSenderAvatar());
        map.put("senderHostel", msg.getSenderHostel());
        map.put("text", msg.getText());
        map.put("time", msg.getCreatedAt() != null ? msg.getCreatedAt().toString() : null);
        map.put("createdAt", msg.getCreatedAt() != null ? msg.getCreatedAt().toString() : null);
        return map;
    }
}
