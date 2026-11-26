package com.tiet.journeyconnect.models;

/**
 * Subclass representing a Train Cohort for intercity travel (e.g. Shatabdi Express to Delhi).
 * Demonstrates Inheritance & Polymorphism:
 *  - Overrides calculatePerRiderFare() to return 0.0 (Self-booking zero ticket-liability model).
 */
public class TrainTravelGroup extends TravelGroup {
    private static final long serialVersionUID = 1L;

    private String communityId;
    private String communityName;
    private String trainNumber;
    private String travelClass;     // CC, EC, 3A, 2A, SL
    private String boardingPoint;

    public TrainTravelGroup() {
        super();
        this.setMaxMembers(50);
    }

    public TrainTravelGroup(String id, String title, String communityId, String communityName, String date, String time, String boardingPoint, String destination, String trainNumber, String travelClass, String adminId) {
        super(id, title, boardingPoint, destination, date, time, 50, adminId);
        this.communityId = communityId;
        this.communityName = communityName;
        this.trainNumber = trainNumber;
        this.travelClass = travelClass;
        this.boardingPoint = boardingPoint;
    }

    /**
     * Polymorphic implementation:
     * Train cohorts operate on a zero ticket-liability self-booking model.
     * Each student books their own IRCTC ticket; the platform coordinates co-riders,
     * so per-rider fare liability is 0.0.
     */
    @Override
    public double calculatePerRiderFare(double totalFare) {
        return 0.0;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getTrainNumber() {
        return trainNumber != null ? trainNumber : "";
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }

    public String getTravelClass() {
        return travelClass != null ? travelClass : "";
    }

    public void setTravelClass(String travelClass) {
        this.travelClass = travelClass;
    }

    public String getBoardingPoint() {
        return boardingPoint != null ? boardingPoint : "Patiala Railway Station";
    }

    public void setBoardingPoint(String boardingPoint) {
        this.boardingPoint = boardingPoint;
    }
}
