package com.tiet.journeyconnect.exceptions;

/**
 * Thrown when an unauthenticated or unauthorized operation is attempted.
 */
public class UnauthorizedAccessException extends RuntimeException {
    private final int statusCode;

    public UnauthorizedAccessException(String message) {
        super(message);
        this.statusCode = 401;
    }

    public UnauthorizedAccessException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
