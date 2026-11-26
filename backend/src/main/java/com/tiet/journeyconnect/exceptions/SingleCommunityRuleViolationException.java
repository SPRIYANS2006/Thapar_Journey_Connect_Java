package com.tiet.journeyconnect.exceptions;

/**
 * Thrown when a student attempts to join multiple hometown communities simultaneously.
 * Enforces the Single-Hometown Membership rule.
 */
public class SingleCommunityRuleViolationException extends RuntimeException {
    public SingleCommunityRuleViolationException(String currentCommunity, String targetCommunity) {
        super("Single-Community Rule: You are currently enrolled in the " + currentCommunity + " Community. You must leave it before joining " + targetCommunity + ".");
    }

    public SingleCommunityRuleViolationException(String message) {
        super(message);
    }
}
