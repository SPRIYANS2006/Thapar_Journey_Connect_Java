package com.tiet.journeyconnect.models;

/**
 * Subclass representing a Bus Travel Cohort for intercity travel (e.g. PRTC / CTU to Chandigarh/Delhi).
 * Demonstrates Inheritance & Polymorphism:
 *  - Overrides calculatePerRiderFare() to return 0.0 (Self-booking zero ticket-liability model).
 */
public class BusTravelGroup extends TravelGroup {
    private static final long serialVersionUID = 1L;

    private String communityId;
    private String communityName;
    private String busOperator;     // e.g. PRTC, Zingbus, CTU, IntrCity
    private String boardingPoint;

    public BusTravelGroup() {
        super();
        this.setMaxMembers(40);
    }

    public BusTravelGroup(String id, String title, String communityId, String communityName, String date, String time, String boardingPoint, String destination, String busOperator, String adminId) {
        super(id, title, boardingPoint, destination, date, time, 40, adminId);
        this.communityId = communityId;
        this.communityName = communityName;
        this.busOperator = busOperator;
        this.boardingPoint = boardingPoint;
    }

    /**
     * Polymorphic implementation:
     * Bus cohorts operate on a zero ticket-liability self-booking model.
     * Platform coordinates student cohorts while ticketing is handled independently by passengers.
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

    public String getBusOperator() {
        return busOperator != null ? busOperator : "";
    }

    public void setBusOperator(String busOperator) {
        this.busOperator = busOperator;
    }

    public String getBoardingPoint() {
        return boardingPoint != null ? boardingPoint : "Patiala Bus Stand";
    }

    public void setBoardingPoint(String boardingPoint) {
        this.boardingPoint = boardingPoint;
    }
}
