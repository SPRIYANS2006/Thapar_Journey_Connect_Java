package com.tiet.journeyconnect.models.entities;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "chat_messages", indexes = {
    @Index(name = "idx_chat_group", columnList = "group_id")
})
public class ChatMessageEntity {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "group_id", nullable = false)
    private String groupId;

    @Column(name = "sender_id", nullable = false)
    private String senderId;

    @Column(name = "sender_name", nullable = false)
    private String senderName;

    @Column(name = "sender_avatar")
    private String senderAvatar = "ST";

    @Column(name = "sender_hostel")
    private String senderHostel = "";

    @Column(name = "text", length = 2000, nullable = false)
    private String text;

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    public ChatMessageEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getSenderAvatar() { return senderAvatar != null ? senderAvatar : "ST"; }
    public void setSenderAvatar(String senderAvatar) { this.senderAvatar = senderAvatar; }

    public String getSenderHostel() { return senderHostel != null ? senderHostel : ""; }
    public void setSenderHostel(String senderHostel) { this.senderHostel = senderHostel; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
