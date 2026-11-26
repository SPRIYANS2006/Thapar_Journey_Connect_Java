package com.tiet.journeyconnect.models.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document(collection = "cohort_travel_groups")
public class TravelGroupEntity {

    @Id
private String id;
private String type; // "Train" or "Bus"
private String title;
private String communityId;
private String communityName;
private String creatorId;
private String creatorName;
private String date;
private String departureTime;
private String boardingPoint;
private String destination;
private String trainNumber = "";
private String travelClass = "";
private String busOperator = "";
private String description = "";
private Integer membersCount = 1;
private String status = "Open";
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
