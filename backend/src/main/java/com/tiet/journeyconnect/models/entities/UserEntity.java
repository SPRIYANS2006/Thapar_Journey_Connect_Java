package com.tiet.journeyconnect.models.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document(collection = "users")
public class UserEntity {

    @Id
private String id;
private String name;
private String email;
private String passwordHash;
private String hostel = "";
private String avatar = "ST";
private String phone = "";
private String degree = "";
private String year = "";
private String hometown = "";
private String rollNumber = "";
private String preferredTransport = "Cab";
private String timeWindow = "Every 15 Minutes";
private String frequentRoutes = ""; // Comma-separated routes
private Boolean rideNotifications = true;
private Boolean disabled = false;
private String mutedUsers = ""; // Comma-separated user IDs
private Integer tokenVersion = 1;
private Double reliability = 5.0;
private Integer tripsCompleted = 0;
private String role = "student";
private String activeGroupId;
private String joinedCommunity;
private Instant createdAt = Instant.now();

    public UserEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getHostel() { return hostel != null ? hostel : ""; }
    public void setHostel(String hostel) { this.hostel = hostel; }

    public String getAvatar() { return avatar != null ? avatar : "ST"; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getPhone() { return phone != null ? phone : ""; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDegree() { return degree != null ? degree : ""; }
    public void setDegree(String degree) { this.degree = degree; }

    public String getYear() { return year != null ? year : ""; }
    public void setYear(String year) { this.year = year; }

    public String getHometown() { return hometown != null ? hometown : ""; }
    public void setHometown(String hometown) { this.hometown = hometown; }

    public String getRollNumber() { return rollNumber != null ? rollNumber : ""; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public String getPreferredTransport() { return preferredTransport != null ? preferredTransport : "Cab"; }
    public void setPreferredTransport(String preferredTransport) { this.preferredTransport = preferredTransport; }

    public String getTimeWindow() { return timeWindow != null ? timeWindow : "Every 15 Minutes"; }
    public void setTimeWindow(String timeWindow) { this.timeWindow = timeWindow; }

    public String getFrequentRoutes() { return frequentRoutes != null ? frequentRoutes : ""; }
    public void setFrequentRoutes(String frequentRoutes) { this.frequentRoutes = frequentRoutes; }

    public Boolean getRideNotifications() { return rideNotifications != null ? rideNotifications : true; }
    public void setRideNotifications(Boolean rideNotifications) { this.rideNotifications = rideNotifications; }

    public Boolean getDisabled() { return disabled != null ? disabled : false; }
    public void setDisabled(Boolean disabled) { this.disabled = disabled; }

    public String getMutedUsers() { return mutedUsers != null ? mutedUsers : ""; }
    public void setMutedUsers(String mutedUsers) { this.mutedUsers = mutedUsers; }

    public Integer getTokenVersion() { return tokenVersion != null ? tokenVersion : 1; }
    public void setTokenVersion(Integer tokenVersion) { this.tokenVersion = tokenVersion; }

    public Double getReliability() { return reliability != null ? reliability : 5.0; }
    public void setReliability(Double reliability) { this.reliability = reliability; }

    public Integer getTripsCompleted() { return tripsCompleted != null ? tripsCompleted : 0; }
    public void setTripsCompleted(Integer tripsCompleted) { this.tripsCompleted = tripsCompleted; }

    public String getRole() { return role != null ? role : "student"; }
    public void setRole(String role) { this.role = role; }

    public String getActiveGroupId() { return activeGroupId; }
    public void setActiveGroupId(String activeGroupId) { this.activeGroupId = activeGroupId; }

    public String getJoinedCommunity() { return joinedCommunity; }
    public void setJoinedCommunity(String joinedCommunity) { this.joinedCommunity = joinedCommunity; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
