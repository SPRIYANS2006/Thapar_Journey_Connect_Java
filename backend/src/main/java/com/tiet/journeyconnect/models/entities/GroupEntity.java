package com.tiet.journeyconnect.models.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document(collection = "travel_groups")
public class GroupEntity {

    @Id
private String id;
private String destinationId;
private String destinationName;
private String departureTime;
private String date;
private String status = "Open";
private String transport = "Shared Cab";
private Integer maxMembers = 4;
private Double adminFare;
private String pickupPoint = "Main Gate";
private String origin = "Thapar Institute";
private String direction = "departing";
private Integer matchScore = 95;
private String slotKey;
private String adminId;
private Instant confirmedAt;
private String confirmedBy;
private Instant completedAt;
private String completedBy;
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
