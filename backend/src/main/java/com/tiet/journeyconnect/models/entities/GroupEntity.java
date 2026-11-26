package com.tiet.journeyconnect.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "travel_groups")
public class GroupEntity {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "destination_id", nullable = false)
    private String destinationId;

    @Column(name = "destination_name", nullable = false)
    private String destinationName;

    @Column(name = "departure_time", nullable = false)
    private String departureTime;

    @Column(name = "date", nullable = false)
    private String date;

    @Column(name = "status")
    private String status = "Open";

    @Column(name = "transport")
    private String transport = "Shared Cab";

    @Column(name = "max_members")
    private Integer maxMembers = 4;

    @Column(name = "admin_fare")
    private Double adminFare;

    @Column(name = "pickup_point")
    private String pickupPoint = "Main Gate";

    @Column(name = "origin")
    private String origin = "Thapar Institute";

    @Column(name = "direction")
    private String direction = "departing";

    @Column(name = "match_score")
    private Integer matchScore = 95;

    @Column(name = "slot_key")
    private String slotKey;

    @Column(name = "admin_id", nullable = false)
    private String adminId;

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

    public GroupEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDestinationId() { return destinationId; }
    public void setDestinationId(String destinationId) { this.destinationId = destinationId; }

    public String getDestinationName() { return destinationName; }
    public void setDestinationName(String destinationName) { this.destinationName = destinationName; }

    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStatus() { return status != null ? status : "Open"; }
    public void setStatus(String status) { this.status = status; }

    public String getTransport() { return transport != null ? transport : "Shared Cab"; }
    public void setTransport(String transport) { this.transport = transport; }

    public Integer getMaxMembers() { return maxMembers != null ? maxMembers : 4; }
    public void setMaxMembers(Integer maxMembers) { this.maxMembers = maxMembers; }

    public Double getAdminFare() { return adminFare; }
    public void setAdminFare(Double adminFare) { this.adminFare = adminFare; }

    public String getPickupPoint() { return pickupPoint != null ? pickupPoint : "Main Gate"; }
    public void setPickupPoint(String pickupPoint) { this.pickupPoint = pickupPoint; }

    public String getOrigin() { return origin != null ? origin : "Thapar Institute"; }
    public void setOrigin(String origin) { this.origin = origin; }

    public String getDirection() { return direction != null ? direction : "departing"; }
    public void setDirection(String direction) { this.direction = direction; }

    public Integer getMatchScore() { return matchScore != null ? matchScore : 95; }
    public void setMatchScore(Integer matchScore) { this.matchScore = matchScore; }

    public String getSlotKey() { return slotKey; }
    public void setSlotKey(String slotKey) { this.slotKey = slotKey; }

    public String getAdminId() { return adminId; }
    public void setAdminId(String adminId) { this.adminId = adminId; }

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
