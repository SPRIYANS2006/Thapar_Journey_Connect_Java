package com.tiet.journeyconnect.models.entities;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "group_members", indexes = {
    @Index(name = "idx_member_target", columnList = "target_id, group_type"),
    @Index(name = "idx_member_user", columnList = "user_id")
})
public class GroupMemberEntity {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "target_id", nullable = false)
    private String targetId; // ID of GroupEntity, CityRideEntity, or TravelGroupEntity

    @Column(name = "group_type", nullable = false)
    private String groupType; // "CAMPUS_GROUP", "CITY_RIDE", "TRAVEL_GROUP"

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "avatar")
    private String avatar = "ST";

    @Column(name = "hostel")
    private String hostel = "";

    @Column(name = "reliability")
    private Double reliability = 5.0;

    @Column(name = "departure_time")
    private String departureTime = "12:00";

    @Column(name = "role")
    private String role = "Member";

    @Column(name = "phone")
    private String phone = "";

    @Column(name = "is_companion")
    private Boolean isCompanion = false;

    @Column(name = "added_by")
    private String addedBy;

    @Column(name = "companion_name")
    private String companionName = "";

    @Column(name = "joined_at")
    private Instant joinedAt = Instant.now();

    public GroupMemberEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTargetId() { return targetId; }
    public void setTargetId(String targetId) { this.targetId = targetId; }

    public String getGroupType() { return groupType; }
    public void setGroupType(String groupType) { this.groupType = groupType; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAvatar() { return avatar != null ? avatar : "ST"; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getHostel() { return hostel != null ? hostel : ""; }
    public void setHostel(String hostel) { this.hostel = hostel; }

    public Double getReliability() { return reliability != null ? reliability : 5.0; }
    public void setReliability(Double reliability) { this.reliability = reliability; }

    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }

    public String getRole() { return role != null ? role : "Member"; }
    public void setRole(String role) { this.role = role; }

    public String getPhone() { return phone != null ? phone : ""; }
    public void setPhone(String phone) { this.phone = phone; }

    public Boolean getIsCompanion() { return isCompanion != null ? isCompanion : false; }
    public void setIsCompanion(Boolean isCompanion) { this.isCompanion = isCompanion; }

    public String getAddedBy() { return addedBy; }
    public void setAddedBy(String addedBy) { this.addedBy = addedBy; }

    public String getCompanionName() { return companionName != null ? companionName : ""; }
    public void setCompanionName(String companionName) { this.companionName = companionName; }

    public Instant getJoinedAt() { return joinedAt; }
    public void setJoinedAt(Instant joinedAt) { this.joinedAt = joinedAt; }
}
