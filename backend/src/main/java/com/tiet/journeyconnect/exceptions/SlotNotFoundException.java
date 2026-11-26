package com.tiet.journeyconnect.exceptions;

/**
 * Thrown when a requested departure slot or group ID cannot be located.
 */
public class SlotNotFoundException extends RuntimeException {
    public SlotNotFoundException(String message) {
        super(message);
    }
}
