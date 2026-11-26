package com.tiet.journeyconnect.models;

import com.tiet.journeyconnect.models.interfaces.Bookable;
import com.tiet.journeyconnect.models.interfaces.Cancellable;
import com.tiet.journeyconnect.models.interfaces.Notifiable;
import com.tiet.journeyconnect.models.interfaces.SearchableRoute;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Base Abstract Class representing a Transit Cohort / Travel Group in Thapar Journey Connect.
 * Demonstrates Core OOP Principles:
 *  - Encapsulation: private attributes, input validation, defensive copying of collections.
 *  - Abstraction: abstract polymorphic method calculatePerRiderFare().
 *  - Interface Realization: Bookable, Cancellable, Notifiable, SearchableRoute.
 */
public abstract class TravelGroup implements Bookable, Cancellable, Notifiable, SearchableRoute, Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private String origin;
    private String destination;
    private String date;             // YYYY-MM-DD
    private String departureTime;     // HH:mm
    private int maxMembers;
    private String status;           // Open, Confirmed, Completed, Cancelled
    private String adminId;
    private final List<RideMember> members = new ArrayList<>();
    private Instant createdAt;
    private Instant confirmedAt;
    private Instant completedAt;

    public TravelGroup() {
        this.status = "Open";
        this.maxMembers = 4;
        this.createdAt = Instant.now();
    }

    public TravelGroup(String id, String title, String origin, String destination, String date, String departureTime, int maxMembers, String adminId) {
        this();
        this.id = id;
        this.title = title;
        this.origin = origin;
        this.destination = destination;
        this.date = date;
        this.departureTime = departureTime;
        this.maxMembers = maxMembers;
        this.adminId = adminId;
    }

    /**
     * Polymorphic method overridden by subclasses (CabRideGroup, TrainTravelGroup, BusTravelGroup)
     * demonstrating Run-Time Polymorphism / Dynamic Method Dispatch in Java.
     *
     * @param totalFare Total group transit fare or estimated total
     * @return Per-rider fare or individual ticket indicator
     */
    public abstract double calculatePerRiderFare(double totalFare);

    // Defensive copying for collection encapsulation
    public List<RideMember> getMembers() {
        return Collections.unmodifiableList(members);
    }

    public synchronized boolean addMember(RideMember member) {
        if (member == null) return false;
        if (members.size() >= maxMembers) return false;
        if (isMemberEnrolled(member.getUserId())) return false;
        return members.add(member);
    }

    public synchronized boolean removeMember(String userId) {
        if (userId == null) return false;
        return members.removeIf(m -> Objects.equals(m.getUserId(), userId) || Objects.equals(m.getAddedBy(), userId));
    }

    public boolean isMemberEnrolled(String userId) {
        if (userId == null) return false;
        return members.stream().anyMatch(m -> Objects.equals(m.getUserId(), userId));
    }

    // Realization of Bookable interface
    @Override
    public boolean isAvailable() {
        return "Open".equalsIgnoreCase(status) && members.size() < maxMembers;
    }

    @Override
    public int getAvailableSeats() {
        return Math.max(0, maxMembers - members.size());
    }

    @Override
    public int getMaxCapacity() {
        return maxMembers;
    }

    // Realization of Cancellable interface
    @Override
    public double calculateCancellationPenalty(double hoursUntilDeparture) {
        if (hoursUntilDeparture >= 0.0 && hoursUntilDeparture <= 0.5) {
            return 0.8; // Late cancellation within 30 minutes
        } else if (hoursUntilDeparture >= 0.0 && hoursUntilDeparture <= 4.0) {
            return 0.3; // Cancellation within 4 hours
        }
        return 0.0;
    }

    @Override
    public boolean canBeCancelled(double hoursUntilDeparture) {
        return !"Completed".equalsIgnoreCase(status) && !"Cancelled".equalsIgnoreCase(status);
    }

    // Realization of Notifiable interface
    @Override
    public String getNotificationTopic() {
        return "group_" + id;
    }

    @Override
    public String getAlertSummary() {
        return "Transit Cohort #" + id + " for " + destination + " at " + departureTime;
    }

    // Realization of SearchableRoute interface
    @Override
    public String getOrigin() {
        return origin != null ? origin : "Thapar Institute";
    }

    @Override
    public String getDestination() {
        return destination;
    }

    @Override
    public String getDate() {
        return date;
    }

    @Override
    public String getDepartureTime() {
        return departureTime;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(int maxMembers) {
        this.maxMembers = Math.max(1, maxMembers);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(Instant confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }
}
