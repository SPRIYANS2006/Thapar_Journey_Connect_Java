package com.tiet.journeyconnect.models;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Encapsulated domain model representing a student rider in any travel group or cab.
 * Demonstrates strict Encapsulation, private fields, and validation in setters/constructors.
 */
public class RideMember implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String userId;
    private String name;
    private String avatar;
    private String hostel;
    private double reliability;
    private String departureTime;
    private String role;
    private String phone;
    private boolean isCompanion;
    private String addedBy;
    private String companionName;
    private Instant joinedAt;

    public RideMember() {
        this.id = UUID.randomUUID().toString();
        this.joinedAt = Instant.now();
        this.avatar = "ST";
        this.reliability = 5.0;
        this.role = "Member";
    }

    public RideMember(String userId, String name, String hostel, String phone, String departureTime, String role) {
        this();
        setUserId(userId);
        setName(name);
        setHostel(hostel);
        setPhone(phone);
        this.departureTime = departureTime != null ? departureTime : "12:00";
        this.role = role != null ? role : "Member";
    }

    // Encapsulation with defensive checks
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be empty");
        }
        this.userId = userId.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Rider name cannot be empty");
        }
        this.name = name.trim();
        if (this.avatar == null || this.avatar.equals("ST")) {
            this.avatar = extractInitials(this.name);
        }
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = (avatar != null && !avatar.trim().isEmpty()) ? avatar : "ST";
    }

    public String getHostel() {
        return hostel != null ? hostel : "";
    }

    public void setHostel(String hostel) {
        this.hostel = hostel != null ? hostel.trim() : "";
    }

    public double getReliability() {
        return reliability;
    }

    public void setReliability(double reliability) {
        this.reliability = Math.max(1.0, Math.min(5.0, reliability));
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getRole() {
        return role != null ? role : "Member";
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhone() {
        return phone != null ? phone : "";
    }

    public void setPhone(String phone) {
        this.phone = phone != null ? phone.trim() : "";
    }

    public boolean isCompanion() {
        return isCompanion;
    }

    public void setCompanion(boolean companion) {
        isCompanion = companion;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public String getCompanionName() {
        return companionName != null ? companionName : "";
    }

    public void setCompanionName(String companionName) {
        this.companionName = companionName;
    }

    public Instant getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Instant joinedAt) {
        this.joinedAt = joinedAt != null ? joinedAt : Instant.now();
    }

    private static String extractInitials(String name) {
        if (name == null || name.trim().isEmpty()) return "ST";
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (!p.isEmpty()) {
                sb.append(Character.toUpperCase(p.charAt(0)));
                if (sb.length() == 2) break;
            }
        }
        return sb.length() > 0 ? sb.toString() : "ST";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RideMember that)) return false;
        return Objects.equals(userId, that.userId) && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, id);
    }
}
