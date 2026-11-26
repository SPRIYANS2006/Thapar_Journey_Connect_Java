package com.tiet.journeyconnect.models;

/**
 * Subclass representing a Shared Cab or Shared Auto group on campus or city routes.
 * Demonstrates Inheritance & Polymorphism:
 *  - Overrides calculatePerRiderFare() to split total cab fare equally among occupied seats.
 */
public class CabRideGroup extends TravelGroup {
    private static final long serialVersionUID = 1L;

    private String destinationId;
    private String transport;       // "Shared Cab" or "Shared Auto"
    private String slotKey;
    private String direction;       // "departing" or "returning"
    private String pickupPoint;     // "Main Gate"
    private Double adminFare;
    private double totalFare;

    public CabRideGroup() {
        super();
        this.transport = "Shared Cab";
        this.direction = "departing";
        this.pickupPoint = "Main Gate";
        this.setMaxMembers(4);
    }

    public CabRideGroup(String id, String destinationId, String destinationName, String date, String time, String transport, int maxMembers, String adminId) {
        super(id, "Ride to " + destinationName, "Thapar Institute", destinationName, date, time, maxMembers, adminId);
        this.destinationId = destinationId;
        this.transport = transport != null ? transport : "Shared Cab";
        this.direction = "departing";
        this.pickupPoint = "Main Gate";
    }

    /**
     * Polymorphic implementation:
     * Cab and Auto travel divide the total vehicle hire cost equally among all booked riders.
     * Example: ₹1400 / 4 riders = ₹350 per rider.
     */
    @Override
    public double calculatePerRiderFare(double totalFareToSplit) {
        double fare = totalFareToSplit > 0 ? totalFareToSplit : (adminFare != null && adminFare > 0 ? adminFare : this.totalFare);
        int ridersCount = getMembers().size();
        if (ridersCount <= 0 || fare <= 0) {
            return 0.0;
        }
        return Math.round((fare / ridersCount) * 100.0) / 100.0;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(String destinationId) {
        this.destinationId = destinationId;
    }

    public String getTransport() {
        return transport != null ? transport : "Shared Cab";
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    public String getSlotKey() {
        return slotKey;
    }

    public void setSlotKey(String slotKey) {
        this.slotKey = slotKey;
    }

    public String getDirection() {
        return direction != null ? direction : "departing";
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getPickupPoint() {
        return pickupPoint != null ? pickupPoint : "Main Gate";
    }

    public void setPickupPoint(String pickupPoint) {
        this.pickupPoint = pickupPoint;
    }

    public Double getAdminFare() {
        return adminFare;
    }

    public void setAdminFare(Double adminFare) {
        this.adminFare = adminFare;
    }

    public double getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(double totalFare) {
        this.totalFare = totalFare;
    }
}
