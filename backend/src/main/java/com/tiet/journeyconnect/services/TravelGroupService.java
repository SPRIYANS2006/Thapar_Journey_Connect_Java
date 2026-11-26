package com.tiet.journeyconnect.services;

import com.tiet.journeyconnect.dto.TravelDtos.*;
import com.tiet.journeyconnect.exceptions.SlotNotFoundException;
import com.tiet.journeyconnect.exceptions.UnauthorizedAccessException;
import com.tiet.journeyconnect.factory.TravelGroupFactory;
import com.tiet.journeyconnect.factory.TravelGroupType;
import com.tiet.journeyconnect.models.TravelGroup;
import com.tiet.journeyconnect.models.entities.*;
import com.tiet.journeyconnect.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
public class TravelGroupService {

    @Autowired
    private TravelGroupRepository travelGroupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private SerializerService serializerService;

    @Autowired
    private RidePolicyService ridePolicyService;

    @Autowired
    private NotificationService notificationService;

    public List<Map<String, Object>> getTravelGroups(String type, String communityId, String search, String date, Boolean excludeJoined, UserEntity user) {
        List<TravelGroupEntity> all = travelGroupRepository.findByStatusOrderByDateAscDepartureTimeAsc("Open");
        List<Map<String, Object>> result = new ArrayList<>();

        for (TravelGroupEntity tg : all) {
            if (type != null && !type.isEmpty() && !"All".equalsIgnoreCase(type) && !tg.getType().equalsIgnoreCase(type)) {
                continue;
            }
            if (communityId != null && !communityId.isEmpty() && !"All".equalsIgnoreCase(communityId) && !tg.getCommunityId().equalsIgnoreCase(communityId)) {
                continue;
            }
            if (date != null && !date.isEmpty() && !"All".equalsIgnoreCase(date) && !tg.getDate().equalsIgnoreCase(date)) {
                continue;
            }

            List<GroupMemberEntity> members = groupMemberRepository.findByTargetIdAndGroupTypeOrderByJoinedAtAsc(tg.getId(), "TRAVEL_GROUP");
            boolean isJoined = members.stream().anyMatch(m -> user.getId().equals(m.getUserId()));

            if (Boolean.TRUE.equals(excludeJoined) && isJoined) {
                continue;
            }

            if (search != null && !search.trim().isEmpty()) {
                String q = search.trim().toLowerCase();
                boolean matches = (tg.getTitle() != null && tg.getTitle().toLowerCase().contains(q)) ||
                        (tg.getDestination() != null && tg.getDestination().toLowerCase().contains(q)) ||
                        (tg.getCommunityName() != null && tg.getCommunityName().toLowerCase().contains(q)) ||
                        (tg.getTrainNumber() != null && tg.getTrainNumber().toLowerCase().contains(q)) ||
                        (tg.getBusOperator() != null && tg.getBusOperator().toLowerCase().contains(q));
                if (!matches) continue;
            }

            result.add(serializerService.serializeTravelGroup(tg, members, user.getId()));
        }

        return result;
    }

    public List<Map<String, Object>> getMyTravelGroups(UserEntity user) {
        List<GroupMemberEntity> memberships = groupMemberRepository.findByUserId(user.getId());
        List<Map<String, Object>> list = new ArrayList<>();

        for (GroupMemberEntity mem : memberships) {
            if ("TRAVEL_GROUP".equals(mem.getGroupType())) {
                Optional<TravelGroupEntity> tgOpt = travelGroupRepository.findById(mem.getTargetId());
                if (tgOpt.isPresent()) {
                    TravelGroupEntity tg = tgOpt.get();
                    if (!"Cancelled".equalsIgnoreCase(tg.getStatus())) {
                        List<GroupMemberEntity> members = groupMemberRepository.findByTargetIdAndGroupTypeOrderByJoinedAtAsc(tg.getId(), "TRAVEL_GROUP");
                        list.add(serializerService.serializeTravelGroup(tg, members, user.getId()));
                    }
                }
            }
        }
        return list;
    }

    public Map<String, Object> getTravelGroupById(String id, UserEntity user) {
        Optional<TravelGroupEntity> tgOpt = travelGroupRepository.findById(id);
        if (tgOpt.isEmpty()) {
            throw new SlotNotFoundException("Travel group not found.");
        }
        TravelGroupEntity tg = tgOpt.get();
        List<GroupMemberEntity> members = groupMemberRepository.findByTargetIdAndGroupTypeOrderByJoinedAtAsc(tg.getId(), "TRAVEL_GROUP");
        return serializerService.serializeTravelGroup(tg, members, user.getId());
    }

    @Transactional
    public Map<String, Object> createTravelGroup(CreateTravelGroupRequest req, UserEntity user) {
        // Enforce campus-wide Single-Ride Policy
        ridePolicyService.validateSingleRidePolicy(user.getId(), null);

        if (req.getType() == null || (!req.getType().equalsIgnoreCase("Train") && !req.getType().equalsIgnoreCase("Bus"))) {
            throw new IllegalArgumentException("Travel group type must be either \"Train\" or \"Bus\".");
        }
        if (req.getTitle() == null || req.getTitle().trim().length() < 3) {
            throw new IllegalArgumentException("Group title must be at least 3 characters long.");
        }
        if (req.getCommunityId() == null || req.getCommunityId().trim().isEmpty()) {
            throw new IllegalArgumentException("Please select a valid destination community.");
        }

        Optional<CommunityEntity> commOpt = communityRepository.findById(req.getCommunityId());
        if (commOpt.isEmpty()) {
            throw new IllegalArgumentException("The selected destination community does not exist.");
        }
        CommunityEntity comm = commOpt.get();

        String typeUpper = req.getType().equalsIgnoreCase("Train") ? "Train" : "Bus";
        String groupId = "tg_" + UUID.randomUUID().toString().substring(0, 8);

        // Demonstrate Factory Pattern usage in business logic
        TravelGroupFactory.GroupSpecification spec = new TravelGroupFactory.GroupSpecification();
        spec.setId(groupId);
        spec.setTitle(req.getTitle().trim());
        spec.setCommunityId(comm.getId());
        spec.setCommunityName(comm.getName());
        spec.setDate(SlotEngineService.normalizeDate(req.getDate()));
        spec.setDepartureTime(req.getDepartureTime() != null ? req.getDepartureTime().trim() : "12:00");
        spec.setBoardingPoint(req.getBoardingPoint() != null ? req.getBoardingPoint().trim() : (typeUpper.equals("Train") ? "Patiala Railway Station" : "Patiala Bus Stand"));
        spec.setDestination(req.getDestination() != null ? req.getDestination().trim() : comm.getName());
        spec.setTrainNumber(req.getTrainNumber());
        spec.setTravelClass(req.getTravelClass());
        spec.setBusOperator(req.getBusOperator());

        TravelGroupType factoryType = typeUpper.equals("Train") ? TravelGroupType.TRAIN : TravelGroupType.BUS;
        TravelGroup domainGroup = TravelGroupFactory.createGroup(factoryType, spec, user.getId(), user.getName(), user.getHostel(), user.getPhone());

        // Persist entity to database
        TravelGroupEntity entity = new TravelGroupEntity();
        entity.setId(groupId);
        entity.setType(typeUpper);
        entity.setTitle(domainGroup.getTitle());
        entity.setCommunityId(comm.getId());
        entity.setCommunityName(comm.getName());
        entity.setCreatorId(user.getId());
        entity.setCreatorName(user.getName());
        entity.setDate(domainGroup.getDate());
        entity.setDepartureTime(domainGroup.getDepartureTime());
        entity.setBoardingPoint(domainGroup.getOrigin());
        entity.setDestination(domainGroup.getDestination());
        entity.setTrainNumber(spec.getTrainNumber());
        entity.setTravelClass(spec.getTravelClass());
        entity.setBusOperator(spec.getBusOperator());
        entity.setDescription(req.getDescription() != null ? req.getDescription().trim() : "");
        entity.setMembersCount(1);
        entity.setStatus("Open");
        entity.setCreatedAt(Instant.now());
        entity = travelGroupRepository.save(entity);

        GroupMemberEntity mem = new GroupMemberEntity();
        mem.setId(UUID.randomUUID().toString());
        mem.setTargetId(entity.getId());
        mem.setGroupType("TRAVEL_GROUP");
        mem.setUserId(user.getId());
        mem.setName(user.getName());
        mem.setAvatar(user.getAvatar());
        mem.setHostel(user.getHostel());
        mem.setReliability(user.getReliability());
        mem.setPhone(user.getPhone());
        mem.setRole("Admin");
        mem.setDepartureTime(entity.getDepartureTime());
        mem.setJoinedAt(Instant.now());
        groupMemberRepository.save(mem);

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("success", true);
        res.put("group", serializerService.serializeTravelGroup(entity, List.of(mem), user.getId()));
        return res;
    }

    @Transactional
    public Map<String, Object> joinTravelGroup(String id, UserEntity user) {
        Optional<TravelGroupEntity> tgOpt = travelGroupRepository.findById(id);
        if (tgOpt.isEmpty()) {
            throw new SlotNotFoundException("Travel group not found.");
        }
        TravelGroupEntity tg = tgOpt.get();

        List<GroupMemberEntity> members = groupMemberRepository.findByTargetIdAndGroupTypeOrderByJoinedAtAsc(tg.getId(), "TRAVEL_GROUP");
        if (members.stream().anyMatch(m -> user.getId().equals(m.getUserId()))) {
            Map<String, Object> res = new LinkedHashMap<>();
            res.put("success", true);
            res.put("message", "You are already enrolled in this travel group.");
            res.put("group", serializerService.serializeTravelGroup(tg, members, user.getId()));
            return res;
        }

        // Enforce campus-wide Single-Ride Policy across all categories
        ridePolicyService.validateSingleRidePolicy(user.getId(), id);

        GroupMemberEntity mem = new GroupMemberEntity();
        mem.setId(UUID.randomUUID().toString());
        mem.setTargetId(tg.getId());
        mem.setGroupType("TRAVEL_GROUP");
        mem.setUserId(user.getId());
        mem.setName(user.getName());
        mem.setAvatar(user.getAvatar());
        mem.setHostel(user.getHostel());
        mem.setReliability(user.getReliability());
        mem.setPhone(user.getPhone());
        mem.setRole("Member");
        mem.setDepartureTime(tg.getDepartureTime());
        mem.setJoinedAt(Instant.now());
        groupMemberRepository.save(mem);

        members.add(mem);
        tg.setMembersCount(members.size());
        travelGroupRepository.save(tg);

        notificationService.broadcastMemberJoined(
                tg.getId(),
                "TRAVEL_GROUP",
                tg.getDestination(),
                user,
                members.size(),
                10
        );

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("success", true);
        res.put("group", serializerService.serializeTravelGroup(tg, members, user.getId()));
        return res;
    }

    @Transactional
    public Map<String, Object> leaveTravelGroup(String id, UserEntity user) {
        Optional<TravelGroupEntity> tgOpt = travelGroupRepository.findById(id);
        if (tgOpt.isEmpty()) {
            throw new SlotNotFoundException("Travel group not found.");
        }
        TravelGroupEntity tg = tgOpt.get();

        groupMemberRepository.deleteByTargetIdAndUserId(tg.getId(), user.getId());
        List<GroupMemberEntity> members = groupMemberRepository.findByTargetIdAndGroupTypeOrderByJoinedAtAsc(tg.getId(), "TRAVEL_GROUP");

        if (members.isEmpty()) {
            travelGroupRepository.delete(tg);
            chatMessageRepository.deleteByGroupId(tg.getId());
        } else {
            tg.setMembersCount(members.size());
            if (user.getId().equals(tg.getCreatorId())) {
                GroupMemberEntity nextAdmin = members.get(0);
                tg.setCreatorId(nextAdmin.getUserId());
                tg.setCreatorName(nextAdmin.getName());
                nextAdmin.setRole("Admin");
                groupMemberRepository.save(nextAdmin);
            }
            travelGroupRepository.save(tg);

            notificationService.broadcastMemberLeft(
                    tg.getId(),
                    "TRAVEL_GROUP",
                    tg.getDestination(),
                    user,
                    members.size(),
                    10
            );
        }

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("success", true);
        res.put("message", "Left travel group.");
        return res;
    }

    public List<Map<String, Object>> getMessages(String id, UserEntity user) {
        List<ChatMessageEntity> messages = chatMessageRepository.findByGroupIdOrderByCreatedAtAsc(id);
        List<Map<String, Object>> list = new ArrayList<>();
        for (ChatMessageEntity m : messages) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", m.getId());
            map.put("groupId", m.getGroupId());
            map.put("senderId", m.getSenderId());
            map.put("senderName", m.getSenderName());
            map.put("senderAvatar", m.getSenderAvatar());
            map.put("senderHostel", m.getSenderHostel());
            map.put("text", m.getText());
            map.put("time", m.getCreatedAt() != null ? m.getCreatedAt().toString() : "Now");
            map.put("createdAt", m.getCreatedAt() != null ? m.getCreatedAt().toString() : null);
            list.add(map);
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
        msg = chatMessageRepository.save(msg);

        Optional<TravelGroupEntity> tgOpt = travelGroupRepository.findById(id);
        String destName = tgOpt.map(TravelGroupEntity::getDestination).orElse("Travel Group");
        notificationService.broadcastChatMessage(id, "TRAVEL_GROUP", destName, msg);

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("success", true);
        res.put("message", serializerService.serializeChatMessage(msg));
        return res;
    }
}
