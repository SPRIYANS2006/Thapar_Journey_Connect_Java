package com.tiet.journeyconnect.dto;

import java.util.List;

public class TravelDtos {

    public static class JoinSlotRequest {
        private String destinationId;
        private String date;
        private String time;
        private String vehicle;
        private String direction;
        private String pickupPoint;

        public JoinSlotRequest() {}

        public String getDestinationId() { return destinationId; }
        public void setDestinationId(String destinationId) { this.destinationId = destinationId; }
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public String getTime() { return time; }
        public void setTime(String time) { this.time = time; }
        public String getVehicle() { return vehicle; }
        public void setVehicle(String vehicle) { this.vehicle = vehicle; }
        public String getDirection() { return direction; }
        public void setDirection(String direction) { this.direction = direction; }
        public String getPickupPoint() { return pickupPoint; }
        public void setPickupPoint(String pickupPoint) { this.pickupPoint = pickupPoint; }
    }

    public static class ReschedulePreviewRequest {
        private String oldGroupId;

        public ReschedulePreviewRequest() {}

        public String getOldGroupId() { return oldGroupId; }
        public void setOldGroupId(String oldGroupId) { this.oldGroupId = oldGroupId; }
    }

    public static class RescheduleSlotRequest {
        private String oldGroupId;
        private String targetDestinationId;
        private String targetDate;
        private String targetTime;
        private String targetVehicle;
        private String targetPickupPoint;
        private String targetDirection;

        public RescheduleSlotRequest() {}

        public String getOldGroupId() { return oldGroupId; }
        public void setOldGroupId(String oldGroupId) { this.oldGroupId = oldGroupId; }
        public String getTargetDestinationId() { return targetDestinationId; }
        public void setTargetDestinationId(String targetDestinationId) { this.targetDestinationId = targetDestinationId; }
        public String getTargetDate() { return targetDate; }
        public void setTargetDate(String targetDate) { this.targetDate = targetDate; }
        public String getTargetTime() { return targetTime; }
        public void setTargetTime(String targetTime) { this.targetTime = targetTime; }
        public String getTargetVehicle() { return targetVehicle; }
        public void setTargetVehicle(String targetVehicle) { this.targetVehicle = targetVehicle; }
        public String getTargetPickupPoint() { return targetPickupPoint; }
        public void setTargetPickupPoint(String targetPickupPoint) { this.targetPickupPoint = targetPickupPoint; }
        public String getTargetDirection() { return targetDirection; }
        public void setTargetDirection(String targetDirection) { this.targetDirection = targetDirection; }
    }

    public static class CreateCityRideRequest {
        private String destination;
        private String date;
        private String time;
        private String mode;
        private String pickupPoint;
        private Double totalFare;
        private Integer maxCapacity;
        private String transitDetails;
        private String notes;
        private String direction;

        public CreateCityRideRequest() {}

        public String getDestination() { return destination; }
        public void setDestination(String destination) { this.destination = destination; }
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public String getTime() { return time; }
        public void setTime(String time) { this.time = time; }
        public String getMode() { return mode; }
        public void setMode(String mode) { this.mode = mode; }
        public String getPickupPoint() { return pickupPoint; }
        public void setPickupPoint(String pickupPoint) { this.pickupPoint = pickupPoint; }
        public Double getTotalFare() { return totalFare; }
        public void setTotalFare(Double totalFare) { this.totalFare = totalFare; }
        public Integer getMaxCapacity() { return maxCapacity; }
        public void setMaxCapacity(Integer maxCapacity) { this.maxCapacity = maxCapacity; }
        public String getTransitDetails() { return transitDetails; }
        public void setTransitDetails(String transitDetails) { this.transitDetails = transitDetails; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
        public String getDirection() { return direction; }
        public void setDirection(String direction) { this.direction = direction; }
    }

    public static class JoinCityCommunitySlotRequest {
        private String destination;
        private String date;
        private String time;
        private String mode;
        private String direction;

        public JoinCityCommunitySlotRequest() {}

        public String getDestination() { return destination; }
        public void setDestination(String destination) { this.destination = destination; }
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public String getTime() { return time; }
        public void setTime(String time) { this.time = time; }
        public String getMode() { return mode; }
        public void setMode(String mode) { this.mode = mode; }
        public String getDirection() { return direction; }
        public void setDirection(String direction) { this.direction = direction; }
    }

    public static class CreateTravelGroupRequest {
        private String type;
        private String title;
        private String communityId;
        private String date;
        private String departureTime;
        private String boardingPoint;
        private String destination;
        private String trainNumber;
        private String travelClass;
        private String busOperator;
        private String description;

        public CreateTravelGroupRequest() {}

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getCommunityId() { return communityId; }
        public void setCommunityId(String communityId) { this.communityId = communityId; }
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public String getDepartureTime() { return departureTime; }
        public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }
        public String getBoardingPoint() { return boardingPoint; }
        public void setBoardingPoint(String boardingPoint) { this.boardingPoint = boardingPoint; }
        public String getDestination() { return destination; }
        public void setDestination(String destination) { this.destination = destination; }
        public String getTrainNumber() { return trainNumber; }
        public void setTrainNumber(String trainNumber) { this.trainNumber = trainNumber; }
        public String getTravelClass() { return travelClass; }
        public void setTravelClass(String travelClass) { this.travelClass = travelClass; }
        public String getBusOperator() { return busOperator; }
        public void setBusOperator(String busOperator) { this.busOperator = busOperator; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class SendMessageRequest {
        private String text;

        public SendMessageRequest() {}

        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
    }

    public static class AddFriendRequest {
        private String name;
        private String phone;
        private String hostel;

        public AddFriendRequest() {}

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getHostel() { return hostel; }
        public void setHostel(String hostel) { this.hostel = hostel; }
    }

    public static class ChangePickupRequest {
        private String pickupPoint;

        public ChangePickupRequest() {}

        public String getPickupPoint() { return pickupPoint; }
        public void setPickupPoint(String pickupPoint) { this.pickupPoint = pickupPoint; }
    }

    public static class SetCapacityRequest {
        private Integer maxMembers;

        public SetCapacityRequest() {}

        public Integer getMaxMembers() { return maxMembers; }
        public void setMaxMembers(Integer maxMembers) { this.maxMembers = maxMembers; }
    }

    public static class SetFareRequest {
        private Double totalFare;

        public SetFareRequest() {}

        public Double getTotalFare() { return totalFare; }
        public void setTotalFare(Double totalFare) { this.totalFare = totalFare; }
    }

    public static class RateGroupRequest {
        private String targetUserId;
        private Double rating;

        public RateGroupRequest() {}

        public String getTargetUserId() { return targetUserId; }
        public void setTargetUserId(String targetUserId) { this.targetUserId = targetUserId; }
        public Double getRating() { return rating; }
        public void setRating(Double rating) { this.rating = rating; }
    }
}
