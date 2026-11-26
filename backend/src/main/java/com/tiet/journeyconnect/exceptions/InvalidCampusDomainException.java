package com.tiet.journeyconnect.exceptions;

/**
 * Thrown when an institutional authentication or registration attempt fails
 * because the provided email address does not belong to the official TIET domain (@thapar.edu).
 */
public class InvalidCampusDomainException extends RuntimeException {
    public InvalidCampusDomainException(String message) {
        super(message);
    }

    public InvalidCampusDomainException(String email, String expectedDomain) {
        super("Access Denied: Only official @" + expectedDomain + " Google/email accounts from TIET are permitted. Personal email (" + email + ") is rejected.");
    }
}
