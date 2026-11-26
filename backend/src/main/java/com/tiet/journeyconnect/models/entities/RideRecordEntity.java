package com.tiet.journeyconnect.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "ride_records")
public class RideRecordEntity {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "group_id", nullable = false)
    private String groupId;

    @Column(name = "destination_id", nullable = false)
    private String destinationId;

    @Column(name = "destination_name", nullable = false)
    private String destinationName;

    @Column(name = "departure_time", nullable = false)
    private String departureTime;

    @Column(name = "date", nullable = false)
    private String date;

    @Column(name = "transport")
    private String transport = "Shared Cab";

    @Column(name = "pickup_point")
    private String pickupPoint = "Main Gate";

    @Column(name = "admin_id", nullable = false)
    private String adminId;

    @Column(name = "confirmed_at")
    private Instant confirmedAt;

    @Column(name = "completed_at")
    private Instant completedAt = Instant.now();

    @Column(name = "completed_by")
    private String completedBy = "admin";

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    public RideRecordEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public String getDestinationId() { return destinationId; }
    public void setDestinationId(String destinationId) { this.destinationId = destinationId; }

    public String getDestinationName() { return destinationName; }
    public void setDestinationName(String destinationName) { this.destinationName = destinationName; }

    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTransport() { return transport; }
    public void setTransport(String transport) { this.transport = transport; }

    public String getPickupPoint() { return pickupPoint; }
    public void setPickupPoint(String pickupPoint) { this.pickupPoint = pickupPoint; }

    public String getAdminId() { return adminId; }
    public void setAdminId(String adminId) { this.adminId = adminId; }

    public Instant getConfirmedAt() { return confirmedAt; }
    public void setConfirmedAt(Instant confirmedAt) { this.confirmedAt = confirmedAt; }

    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }

    public String getCompletedBy() { return completedBy; }
    public void setCompletedBy(String completedBy) { this.completedBy = completedBy; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
