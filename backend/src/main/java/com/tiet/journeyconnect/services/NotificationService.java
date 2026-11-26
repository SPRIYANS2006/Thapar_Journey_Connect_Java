package com.tiet.journeyconnect.services;

import com.tiet.journeyconnect.models.entities.ChatMessageEntity;
import com.tiet.journeyconnect.models.entities.GroupMemberEntity;
import com.tiet.journeyconnect.models.entities.UserEntity;
import com.tiet.journeyconnect.repositories.GroupMemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * High-performance, real-time notification engine for Thapar Journey Connect.
 * Provides SSE (Server-Sent Events) streaming alongside short-poll fallback
 * to broadcast real-time toast popups, chat updates, and ride state changes.
 */
@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private SerializerService serializerService;

    // Registry of active SSE emitters by user ID
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<SseEmitter>> userEmitters = new ConcurrentHashMap<>();

    // In-memory circular buffer of recent notifications for polling fallback
    private final ConcurrentLinkedDeque<EventRecord> recentEvents = new ConcurrentLinkedDeque<>();
    private static final int MAX_BUFFER_SIZE = 1000;

    public static class EventRecord {
        private final String id;
        private final long timestamp;
        private final Set<String> targetUserIds;
        private final String eventName;
        private final Object payload;

        public EventRecord(String id, long timestamp, Set<String> targetUserIds, String eventName, Object payload) {
            this.id = id;
            this.timestamp = timestamp;
            this.targetUserIds = targetUserIds;
            this.eventName = eventName;
            this.payload = payload;
        }

        public String getId() { return id; }
        public long getTimestamp() { return timestamp; }
        public Set<String> getTargetUserIds() { return targetUserIds; }
        public String getEventName() { return eventName; }
        public Object getPayload() { return payload; }
    }

    /**
     * Registers an SSE emitter for a connected user session.
     */
    public SseEmitter registerEmitter(String userId) {
        // 30-minute timeout
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);
        CopyOnWriteArrayList<SseEmitter> list = userEmitters.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>());
        list.add(emitter);

        Runnable cleanup = () -> {
            list.remove(emitter);
            if (list.isEmpty()) {
                userEmitters.remove(userId);
            }
        };

        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(e -> cleanup.run());

        try {
            Map<String, Object> welcome = new LinkedHashMap<>();
            welcome.put("status", "connected");
            welcome.put("userId", userId);
            welcome.put("serverTime", System.currentTimeMillis());
            emitter.send(SseEmitter.event().name("connect").data(welcome));
        } catch (IOException e) {
            cleanup.run();
        }

        return emitter;
    }

    /**
     * Delivers an event directly to a targeted user (SSE + Polling Buffer).
     */
    public void sendToUser(String userId, String eventName, Object data) {
        if (userId == null) return;
        String eventId = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();

        EventRecord record = new EventRecord(eventId, now, Set.of(userId), eventName, data);
        storeEvent(record);

        CopyOnWriteArrayList<SseEmitter> emitters = userEmitters.get(userId);
        if (emitters != null) {
            List<SseEmitter> deadEmitters = new ArrayList<>();
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event().id(eventId).name(eventName).data(data));
                } catch (Exception e) {
                    deadEmitters.add(emitter);
                }
            }
            if (!deadEmitters.isEmpty()) {
                emitters.removeAll(deadEmitters);
            }
        }
    }

    /**
     * Broadcasts a new chat message to all other riders in the group.
     */
    public void broadcastChatMessage(String groupId, String groupType, String destinationName, ChatMessageEntity msg) {
        if (groupId == null || msg == null) return;
        List<GroupMemberEntity> members = groupMemberRepository.findByTargetIdAndGroupTypeOrderByJoinedAtAsc(groupId, groupType);

        for (GroupMemberEntity m : members) {
            if (m.getUserId() != null && !m.getUserId().equals(msg.getSenderId())) {
                // Toast popup payload for App.jsx
                Map<String, Object> toastData = new LinkedHashMap<>();
                toastData.put("groupId", groupId);
                toastData.put("destinationName", destinationName != null ? destinationName : "Ride");
                toastData.put("senderId", msg.getSenderId());
                toastData.put("senderName", msg.getSenderName());
                toastData.put("avatar", msg.getSenderAvatar());
                toastData.put("text", msg.getText());
                toastData.put("time", "Just now");
                sendToUser(m.getUserId(), "new_chat_message", toastData);

                // Modal real-time message stream for GroupChatModal / TravelGroupChatModal
                Map<String, Object> modalData = new LinkedHashMap<>();
                modalData.put("groupId", groupId);
                modalData.put("message", serializerService.serializeChatMessage(msg));
                sendToUser(m.getUserId(), "new_message", modalData);

                if ("TRAVEL_GROUP".equals(groupType)) {
                    sendToUser(m.getUserId(), "new_travel_group_message", modalData);
                }
            }
        }
    }

    /**
     * Broadcasts a "New rider joined" event to all existing group members.
     */
    public void broadcastMemberJoined(String groupId, String groupType, String destinationName, UserEntity joiner, int membersCount, int maxMembers) {
        if (groupId == null || joiner == null) return;
        List<GroupMemberEntity> members = groupMemberRepository.findByTargetIdAndGroupTypeOrderByJoinedAtAsc(groupId, groupType);

        for (GroupMemberEntity m : members) {
            if (m.getUserId() != null && !m.getUserId().equals(joiner.getId())) {
                Map<String, Object> joinData = new LinkedHashMap<>();
                joinData.put("groupId", groupId);
                joinData.put("destinationName", destinationName != null ? destinationName : "Ride");
                joinData.put("joinerId", joiner.getId());
                joinData.put("joinerName", joiner.getName());
                joinData.put("joinerHostel", joiner.getHostel() != null ? joiner.getHostel() : "Campus");
                joinData.put("membersCount", membersCount);
                joinData.put("maxMembers", maxMembers);
                sendToUser(m.getUserId(), "member_joined", joinData);

                // Also notify group update so screens refresh automatically
                Map<String, Object> updateData = new LinkedHashMap<>();
                updateData.put("groupId", groupId);
                sendToUser(m.getUserId(), "group_updated", updateData);
            }
        }
    }

    /**
     * Broadcasts a "Rider left" event to all remaining group members.
     */
    public void broadcastMemberLeft(String groupId, String groupType, String destinationName, UserEntity leaver, int remainingMembersCount, int maxMembers) {
        if (groupId == null || leaver == null) return;
        List<GroupMemberEntity> members = groupMemberRepository.findByTargetIdAndGroupTypeOrderByJoinedAtAsc(groupId, groupType);

        for (GroupMemberEntity m : members) {
            if (m.getUserId() != null && !m.getUserId().equals(leaver.getId())) {
                Map<String, Object> leaveData = new LinkedHashMap<>();
                leaveData.put("groupId", groupId);
                leaveData.put("destinationName", destinationName != null ? destinationName : "Ride");
                leaveData.put("leaverId", leaver.getId());
                leaveData.put("leaverName", leaver.getName());
                leaveData.put("remainingMembersCount", remainingMembersCount);
                leaveData.put("maxMembers", maxMembers);
                sendToUser(m.getUserId(), "member_left", leaveData);

                Map<String, Object> updateData = new LinkedHashMap<>();
                updateData.put("groupId", groupId);
                sendToUser(m.getUserId(), "group_updated", updateData);
            }
        }
    }

    /**
     * Broadcasts "Group confirmed" event to all group members.
     */
    public void broadcastGroupConfirmed(String groupId, String groupType, String destinationName) {
        if (groupId == null) return;
        List<GroupMemberEntity> members = groupMemberRepository.findByTargetIdAndGroupTypeOrderByJoinedAtAsc(groupId, groupType);

        for (GroupMemberEntity m : members) {
            if (m.getUserId() != null) {
                Map<String, Object> confData = new LinkedHashMap<>();
                confData.put("groupId", groupId);
                confData.put("destinationName", destinationName != null ? destinationName : "Ride");
                sendToUser(m.getUserId(), "group_confirmed", confData);

                Map<String, Object> updateData = new LinkedHashMap<>();
                updateData.put("groupId", groupId);
                sendToUser(m.getUserId(), "group_updated", updateData);
            }
        }
    }

    /**
     * Retrieves missed events for polling fallback since the given timestamp.
     */
    public List<Map<String, Object>> getEventsForUser(String userId, long since) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (userId == null) return result;

        for (EventRecord r : recentEvents) {
            if (r.getTimestamp() > since) {
                if (r.getTargetUserIds() == null || r.getTargetUserIds().isEmpty() || r.getTargetUserIds().contains(userId)) {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("id", r.getId());
                    item.put("name", r.getEventName());
                    item.put("data", r.getPayload());
                    item.put("timestamp", r.getTimestamp());
                    result.add(item);
                }
            }
        }
        return result;
    }

    private void storeEvent(EventRecord record) {
        recentEvents.addLast(record);
        while (recentEvents.size() > MAX_BUFFER_SIZE) {
            recentEvents.pollFirst();
        }
    }
}
