package com.tiet.journeyconnect.exceptions;

/**
 * Thrown when a student attempts to enrol in two simultaneous active travel slots or rides.
 * Enforces the Single-Ride Policy on campus.
 */
public class SingleRidePolicyViolationException extends RuntimeException {
    public SingleRidePolicyViolationException(String message) {
        super(message);
    }

    public SingleRidePolicyViolationException(String existingGroupId, String destinationName) {
        super("Single-Ride Policy: You are already enrolled in active Group #" + existingGroupId + " (" + destinationName + "). Please leave or reschedule it before joining another ride.");
    }
}
