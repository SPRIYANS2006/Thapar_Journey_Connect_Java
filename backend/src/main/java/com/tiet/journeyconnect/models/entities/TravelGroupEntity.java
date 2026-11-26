package com.tiet.journeyconnect.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "cohort_travel_groups")
public class TravelGroupEntity {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "type", nullable = false)
    private String type; // "Train" or "Bus"

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "community_id", nullable = false)
    private String communityId;

    @Column(name = "community_name", nullable = false)
    private String communityName;

    @Column(name = "creator_id", nullable = false)
    private String creatorId;

    @Column(name = "creator_name", nullable = false)
    private String creatorName;

    @Column(name = "date", nullable = false)
    private String date;

    @Column(name = "departure_time", nullable = false)
    private String departureTime;

    @Column(name = "boarding_point", nullable = false)
    private String boardingPoint;

    @Column(name = "destination", nullable = false)
    private String destination;

    @Column(name = "train_number")
    private String trainNumber = "";

    @Column(name = "travel_class")
    private String travelClass = "";

    @Column(name = "bus_operator")
    private String busOperator = "";

    @Column(name = "description", length = 1000)
    private String description = "";

    @Column(name = "members_count")
    private Integer membersCount = 1;

    @Column(name = "status")
    private String status = "Open";

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    public TravelGroupEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCommunityId() { return communityId; }
    public void setCommunityId(String communityId) { this.communityId = communityId; }

    public String getCommunityName() { return communityName; }
    public void setCommunityName(String communityName) { this.communityName = communityName; }

    public String getCreatorId() { return creatorId; }
    public void setCreatorId(String creatorId) { this.creatorId = creatorId; }

    public String getCreatorName() { return creatorName; }
    public void setCreatorName(String creatorName) { this.creatorName = creatorName; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }

    public String getBoardingPoint() { return boardingPoint; }
    public void setBoardingPoint(String boardingPoint) { this.boardingPoint = boardingPoint; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getTrainNumber() { return trainNumber != null ? trainNumber : ""; }
    public void setTrainNumber(String trainNumber) { this.trainNumber = trainNumber; }

    public String getTravelClass() { return travelClass != null ? travelClass : ""; }
    public void setTravelClass(String travelClass) { this.travelClass = travelClass; }

    public String getBusOperator() { return busOperator != null ? busOperator : ""; }
    public void setBusOperator(String busOperator) { this.busOperator = busOperator; }

    public String getDescription() { return description != null ? description : ""; }
    public void setDescription(String description) { this.description = description; }

    public Integer getMembersCount() { return membersCount != null ? membersCount : 1; }
    public void setMembersCount(Integer membersCount) { this.membersCount = membersCount; }

    public String getStatus() { return status != null ? status : "Open"; }
    public void setStatus(String status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
