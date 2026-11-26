package com.tiet.journeyconnect.models.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document(collection = "city_rides")
public class CityRideEntity {

    @Id
private String id;
private String communityId;
private String communityName;
private String creatorId;
private String creatorName;
private String origin = "Thapar Campus (Main Gate)";
private String destination;
private String date;
private String time;
private String mode = "Cab"; // "Cab" or "Train"
private String pickupPoint = "Main Gate";
private Double adminFare;
private Double totalFare = 0.0;
private Integer maxCapacity = 4;
private Integer currentMembersCount = 1;
private String transitDetails = "";
private String notes = "";
private String status = "Open";
private String direction = "departing";
private String slotKey;
private Instant confirmedAt;
private String confirmedBy;
private Instant completedAt;
private String completedBy;
private Instant createdAt = Instant.now();

    public CityRideEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCommunityId() { return communityId; }
    public void setCommunityId(String communityId) { this.communityId = communityId; }

    public String getCommunityName() { return communityName; }
    public void setCommunityName(String communityName) { this.communityName = communityName; }

    public String getCreatorId() { return creatorId; }
    public void setCreatorId(String creatorId) { this.creatorId = creatorId; }

    public String getCreatorName() { return creatorName; }
    public void setCreatorName(String creatorName) { this.creatorName = creatorName; }

    public String getOrigin() { return origin != null ? origin : "Thapar Campus (Main Gate)"; }
    public void setOrigin(String origin) { this.origin = origin; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getMode() { return mode != null ? mode : "Cab"; }
    public void setMode(String mode) { this.mode = mode; }

    public String getPickupPoint() { return pickupPoint != null ? pickupPoint : "Main Gate"; }
    public void setPickupPoint(String pickupPoint) { this.pickupPoint = pickupPoint; }

    public Double getAdminFare() { return adminFare; }
    public void setAdminFare(Double adminFare) { this.adminFare = adminFare; }

    public Double getTotalFare() { return totalFare != null ? totalFare : 0.0; }
    public void setTotalFare(Double totalFare) { this.totalFare = totalFare; }

    public Integer getMaxCapacity() { return maxCapacity != null ? maxCapacity : 4; }
    public void setMaxCapacity(Integer maxCapacity) { this.maxCapacity = maxCapacity; }

    public Integer getCurrentMembersCount() { return currentMembersCount != null ? currentMembersCount : 1; }
    public void setCurrentMembersCount(Integer currentMembersCount) { this.currentMembersCount = currentMembersCount; }

    public String getTransitDetails() { return transitDetails != null ? transitDetails : ""; }
    public void setTransitDetails(String transitDetails) { this.transitDetails = transitDetails; }

    public String getNotes() { return notes != null ? notes : ""; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getStatus() { return status != null ? status : "Open"; }
    public void setStatus(String status) { this.status = status; }

    public String getDirection() { return direction != null ? direction : "departing"; }
    public void setDirection(String direction) { this.direction = direction; }

    public String getSlotKey() { return slotKey; }
    public void setSlotKey(String slotKey) { this.slotKey = slotKey; }

    public Instant getConfirmedAt() { return confirmedAt; }
    public void setConfirmedAt(Instant confirmedAt) { this.confirmedAt = confirmedAt; }

    public String getConfirmedBy() { return confirmedBy; }
    public void setConfirmedBy(String confirmedBy) { this.confirmedBy = confirmedBy; }

    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }

    public String getCompletedBy() { return completedBy; }
    public void setCompletedBy(String completedBy) { this.completedBy = completedBy; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
