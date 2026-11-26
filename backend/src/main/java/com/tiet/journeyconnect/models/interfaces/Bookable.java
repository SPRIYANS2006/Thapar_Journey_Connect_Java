package com.tiet.journeyconnect.models.interfaces;

/**
 * Abstraction for any transit entity that can be booked by a university student.
 * Demonstrates Abstraction & Interface design in Java OOP Lab Evaluation.
 */
public interface Bookable {
    boolean isAvailable();
    int getAvailableSeats();
    int getMaxCapacity();
}
