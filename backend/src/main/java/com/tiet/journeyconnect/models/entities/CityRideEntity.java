package com.tiet.journeyconnect.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "city_rides")
public class CityRideEntity {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "community_id", nullable = false)
    private String communityId;

    @Column(name = "community_name", nullable = false)
    private String communityName;

    @Column(name = "creator_id", nullable = false)
    private String creatorId;

    @Column(name = "creator_name", nullable = false)
    private String creatorName;

    @Column(name = "origin")
    private String origin = "Thapar Campus (Main Gate)";

    @Column(name = "destination", nullable = false)
    private String destination;

    @Column(name = "date", nullable = false)
    private String date;

    @Column(name = "time", nullable = false)
    private String time;

    @Column(name = "mode", nullable = false)
    private String mode = "Cab"; // "Cab" or "Train"

    @Column(name = "pickup_point")
    private String pickupPoint = "Main Gate";

    @Column(name = "admin_fare")
    private Double adminFare;

    @Column(name = "total_fare")
    private Double totalFare = 0.0;

    @Column(name = "max_capacity")
    private Integer maxCapacity = 4;

    @Column(name = "current_members_count")
    private Integer currentMembersCount = 1;

    @Column(name = "transit_details")
    private String transitDetails = "";

    @Column(name = "notes", length = 1000)
    private String notes = "";

    @Column(name = "status")
    private String status = "Open";

    @Column(name = "direction")
    private String direction = "departing";

    @Column(name = "slot_key")
    private String slotKey;

    @Column(name = "confirmed_at")
    private Instant confirmedAt;

    @Column(name = "confirmed_by")
    private String confirmedBy;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "completed_by")
    private String completedBy;

    @Column(name = "created_at")
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
