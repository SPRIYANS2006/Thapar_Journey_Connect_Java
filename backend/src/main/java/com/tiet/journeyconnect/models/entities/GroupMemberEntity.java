package com.tiet.journeyconnect.models.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document(collection = "group_members")
public class GroupMemberEntity {

    @Id
private String id;
private String targetId; // ID of GroupEntity, CityRideEntity, or TravelGroupEntity
private String groupType; // "CAMPUS_GROUP", "CITY_RIDE", "TRAVEL_GROUP"
private String userId;
private String name;
private String avatar = "ST";
private String hostel = "";
private Double reliability = 5.0;
private String departureTime = "12:00";
private String role = "Member";
private String phone = "";
private Boolean isCompanion = false;
private String addedBy;
private String companionName = "";
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
