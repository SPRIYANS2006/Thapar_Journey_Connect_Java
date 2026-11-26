package com.tiet.journeyconnect.models.interfaces;

/**
 * Interface representing searchable and filterable route parameters.
 */
public interface SearchableRoute {
    String getOrigin();
    String getDestination();
    String getDate();
    String getDepartureTime();
}
