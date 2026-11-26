package com.tiet.journeyconnect.models.interfaces;

/**
 * Interface representing notification dispatching capability for travel groups.
 */
public interface Notifiable {
    String getNotificationTopic();
    String getAlertSummary();
}
