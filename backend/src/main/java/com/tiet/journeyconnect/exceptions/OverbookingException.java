package com.tiet.journeyconnect.exceptions;

/**
 * Thrown when a rider attempts to join a travel group, cab slot, or cohort
 * that has already reached its maximum vehicle capacity.
 */
public class OverbookingException extends RuntimeException {
    public OverbookingException(String message) {
        super(message);
    }

    public OverbookingException(int currentMembers, int maxCapacity) {
        super("This slot is now full (" + currentMembers + "/" + maxCapacity + " seats filled). Please select another slot.");
    }
}
