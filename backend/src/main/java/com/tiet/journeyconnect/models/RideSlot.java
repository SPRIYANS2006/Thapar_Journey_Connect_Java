package com.tiet.journeyconnect.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Domain model representing one of the 96 discrete 15-minute departure intervals in a 24-hour day.
 * Implements Comparable<RideSlot> for strict chronological ordering in TreeSet collections.
 */
public class RideSlot implements Comparable<RideSlot>, Serializable {
    private static final long serialVersionUID = 1L;

    private String slotKey;
    private String time;                    // "HH:mm" (e.g. "00:00", "00:15", ..., "23:45")
    private String date;                    // "YYYY-MM-DD"
    private String destinationId;
    private String destinationName;
    private String origin;
    private String direction;               // "departing" or "returning"
    private String vehicle;                 // "Shared Cab", "Shared Auto"
    private int maxSeats;
    private int seatsFilled;
    private boolean isFull;
    private boolean isJoined;
    private boolean isPast;
    private boolean isFillingFast;
    private Double estimatedFarePerPerson;
    private Double totalFare;
    private String groupId;
    private String status;
    private final List<SlotMemberView> members = new ArrayList<>();

    public RideSlot() {
        this.maxSeats = 4;
        this.seatsFilled = 0;
        this.status = "Open";
    }

    public RideSlot(String slotKey, String time, String date, String destinationId, String destinationName, String origin, String direction, String vehicle) {
        this();
        this.slotKey = slotKey;
        this.time = time;
        this.date = date;
        this.destinationId = destinationId;
        this.destinationName = destinationName;
        this.origin = origin;
        this.direction = direction;
        this.vehicle = vehicle;
    }

    /**
     * Implements Comparable<RideSlot> to order slots chronologically by departure time.
     * Required for Java Collections Framework (TreeSet<RideSlot>) demonstration in OOP Lab.
     */
    @Override
    public int compareTo(RideSlot other) {
        if (other == null) return 1;
        int timeCompare = this.time.compareTo(other.time);
        if (timeCompare != 0) return timeCompare;
        return this.slotKey.compareTo(other.slotKey);
    }

    public List<SlotMemberView> getMembers() {
        return Collections.unmodifiableList(members);
    }

    public void addMember(SlotMemberView member) {
        if (member != null) {
            this.members.add(member);
            this.seatsFilled = this.members.size();
            this.isFull = this.seatsFilled >= this.maxSeats;
            this.isFillingFast = this.seatsFilled >= 2 && !this.isFull;
        }
    }

    public void setMembersList(List<SlotMemberView> list) {
        this.members.clear();
        if (list != null) {
            this.members.addAll(list);
        }
        this.seatsFilled = this.members.size();
        this.isFull = this.seatsFilled >= this.maxSeats;
        this.isFillingFast = this.seatsFilled >= 2 && !this.isFull;
    }

    // Getters and Setters
    public String getSlotKey() {
        return slotKey;
    }

    public void setSlotKey(String slotKey) {
        this.slotKey = slotKey;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(String destinationId) {
        this.destinationId = destinationId;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public int getMaxSeats() {
        return maxSeats;
    }

    public void setMaxSeats(int maxSeats) {
        this.maxSeats = maxSeats;
    }

    public int getSeatsFilled() {
        return seatsFilled;
    }

    public void setSeatsFilled(int seatsFilled) {
        this.seatsFilled = seatsFilled;
    }

    public boolean isFull() {
        return isFull;
    }

    public void setFull(boolean full) {
        isFull = full;
    }

    public boolean isJoined() {
        return isJoined;
    }

    public void setJoined(boolean joined) {
        isJoined = joined;
    }

    public boolean isPast() {
        return isPast;
    }

    public void setPast(boolean past) {
        isPast = past;
    }

    public boolean isFillingFast() {
        return isFillingFast;
    }

    public void setFillingFast(boolean fillingFast) {
        isFillingFast = fillingFast;
    }

    public Double getEstimatedFarePerPerson() {
        return estimatedFarePerPerson;
    }

    public void setEstimatedFarePerPerson(Double estimatedFarePerPerson) {
        this.estimatedFarePerPerson = estimatedFarePerPerson;
    }

    public Double getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(Double totalFare) {
        this.totalFare = totalFare;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RideSlot rideSlot)) return false;
        return Objects.equals(slotKey, rideSlot.slotKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slotKey);
    }

    /**
     * DTO view of a member in a public slot. Masks sensitive info for privacy.
     */
    public static class SlotMemberView implements Serializable {
        private static final long serialVersionUID = 1L;

        private String id;
        private String name;
        private String avatar;
        private String hostel;
        private String role;
        private double reliability;

        public SlotMemberView() {}

        public SlotMemberView(String id, String name, String avatar, String hostel, String role, double reliability) {
            this.id = id;
            this.name = name;
            this.avatar = avatar;
            this.hostel = hostel;
            this.role = role;
            this.reliability = reliability;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getHostel() {
            return hostel;
        }

        public void setHostel(String hostel) {
            this.hostel = hostel;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public double getReliability() {
            return reliability;
        }

        public void setReliability(double reliability) {
            this.reliability = reliability;
        }
    }
}
