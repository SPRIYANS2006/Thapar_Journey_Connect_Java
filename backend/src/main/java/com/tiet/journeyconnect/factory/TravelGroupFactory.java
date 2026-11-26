package com.tiet.journeyconnect.factory;

import com.tiet.journeyconnect.models.*;

import java.util.UUID;

/**
 * Factory Design Pattern implementation for dynamically creating polymorphically
 * specialized TravelGroup instances (CabRideGroup, TrainTravelGroup, BusTravelGroup).
 * Demonstrates Creational Design Patterns in Java OOP Lab Evaluation.
 */
public class TravelGroupFactory {

    public static class GroupSpecification {
        private String id;
        private String title;
        private String origin;
        private String destination;
        private String destinationId;
        private String communityId;
        private String communityName;
        private String date;
        private String departureTime;
        private String transport;
        private int maxMembers;
        private String trainNumber;
        private String travelClass;
        private String busOperator;
        private String boardingPoint;
        private Double adminFare;

        public GroupSpecification() {}

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getOrigin() { return origin; }
        public void setOrigin(String origin) { this.origin = origin; }
        public String getDestination() { return destination; }
        public void setDestination(String destination) { this.destination = destination; }
        public String getDestinationId() { return destinationId; }
        public void setDestinationId(String destinationId) { this.destinationId = destinationId; }
        public String getCommunityId() { return communityId; }
        public void setCommunityId(String communityId) { this.communityId = communityId; }
        public String getCommunityName() { return communityName; }
        public void setCommunityName(String communityName) { this.communityName = communityName; }
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public String getDepartureTime() { return departureTime; }
        public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }
        public String getTransport() { return transport; }
        public void setTransport(String transport) { this.transport = transport; }
        public int getMaxMembers() { return maxMembers; }
        public void setMaxMembers(int maxMembers) { this.maxMembers = maxMembers; }
        public String getTrainNumber() { return trainNumber; }
        public void setTrainNumber(String trainNumber) { this.trainNumber = trainNumber; }
        public String getTravelClass() { return travelClass; }
        public void setTravelClass(String travelClass) { this.travelClass = travelClass; }
        public String getBusOperator() { return busOperator; }
        public void setBusOperator(String busOperator) { this.busOperator = busOperator; }
        public String getBoardingPoint() { return boardingPoint; }
        public void setBoardingPoint(String boardingPoint) { this.boardingPoint = boardingPoint; }
        public Double getAdminFare() { return adminFare; }
        public void setAdminFare(Double adminFare) { this.adminFare = adminFare; }
    }

    /**
     * Factory method creating appropriate TravelGroup subtype based on TravelGroupType.
     */
    public static TravelGroup createGroup(TravelGroupType type, GroupSpecification spec, String creatorId, String creatorName, String creatorHostel, String creatorPhone) {
        String groupId = spec.getId() != null ? spec.getId() : UUID.randomUUID().toString();
        TravelGroup group;

        switch (type) {
            case CAB -> {
                CabRideGroup cab = new CabRideGroup(
                    groupId,
                    spec.getDestinationId() != null ? spec.getDestinationId() : "dest",
                    spec.getDestination() != null ? spec.getDestination() : "Transit Hub",
                    spec.getDate(),
                    spec.getDepartureTime(),
                    spec.getTransport() != null ? spec.getTransport() : "Shared Cab",
                    spec.getMaxMembers() > 0 ? spec.getMaxMembers() : 4,
                    creatorId
                );
                cab.setAdminFare(spec.getAdminFare());
                if (spec.getOrigin() != null) cab.setOrigin(spec.getOrigin());
                group = cab;
            }
            case TRAIN -> {
                TrainTravelGroup train = new TrainTravelGroup(
                    groupId,
                    spec.getTitle() != null ? spec.getTitle() : "Train Cohort to " + spec.getDestination(),
                    spec.getCommunityId(),
                    spec.getCommunityName(),
                    spec.getDate(),
                    spec.getDepartureTime(),
                    spec.getBoardingPoint() != null ? spec.getBoardingPoint() : "Patiala Railway Station",
                    spec.getDestination(),
                    spec.getTrainNumber(),
                    spec.getTravelClass(),
                    creatorId
                );
                group = train;
            }
            case BUS -> {
                BusTravelGroup bus = new BusTravelGroup(
                    groupId,
                    spec.getTitle() != null ? spec.getTitle() : "Bus Cohort to " + spec.getDestination(),
                    spec.getCommunityId(),
                    spec.getCommunityName(),
                    spec.getDate(),
                    spec.getDepartureTime(),
                    spec.getBoardingPoint() != null ? spec.getBoardingPoint() : "Patiala Bus Stand",
                    spec.getDestination(),
                    spec.getBusOperator(),
                    creatorId
                );
                group = bus;
            }
            default -> throw new IllegalArgumentException("Unsupported TravelGroupType: " + type);
        }

        // Add creator as initial member
        if (creatorId != null && creatorName != null) {
            RideMember creatorMember = new RideMember(
                creatorId,
                creatorName,
                creatorHostel != null ? creatorHostel : "",
                creatorPhone != null ? creatorPhone : "",
                spec.getDepartureTime(),
                "Group Admin"
            );
            group.addMember(creatorMember);
        }

        return group;
    }
}
