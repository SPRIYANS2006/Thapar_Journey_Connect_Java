package com.tiet.journeyconnect.models.interfaces;

/**
 * Interface representing cancellation policies and reliability score adjustments.
 */
public interface Cancellable {
    double calculateCancellationPenalty(double hoursUntilDeparture);
    boolean canBeCancelled(double hoursUntilDeparture);
}
