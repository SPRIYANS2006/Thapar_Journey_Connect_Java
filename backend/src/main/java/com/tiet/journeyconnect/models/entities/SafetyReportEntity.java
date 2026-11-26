package com.tiet.journeyconnect.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "safety_reports")
public class SafetyReportEntity {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "reporter_id", nullable = false)
    private String reporterId;

    @Column(name = "target_user_id")
    private String targetUserId = "";

    @Column(name = "group_id")
    private String groupId = "";

    @Column(name = "category", nullable = false)
    private String category; // 'Safety', 'Privacy / deletion', 'Rating appeal', 'Other'

    @Column(name = "details", length = 3000, nullable = false)
    private String details;

    @Column(name = "status")
    private String status = "Open"; // 'Open', 'Reviewed'

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    public SafetyReportEntity() {}

    public String getId() { return id; }
    public String get_id() { return id; }
    public void setId(String id) { this.id = id; }

    public String getReporterId() { return reporterId; }
    public void setReporterId(String reporterId) { this.reporterId = reporterId; }

    public String getTargetUserId() { return targetUserId; }
    public void setTargetUserId(String targetUserId) { this.targetUserId = targetUserId; }

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getStatus() { return status != null ? status : "Open"; }
    public void setStatus(String status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
