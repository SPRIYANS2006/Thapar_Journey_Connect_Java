package com.tiet.journeyconnect.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "destinations")
public class DestinationEntity {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "icon")
    private String icon = "📍";

    @Column(name = "description", length = 500)
    private String description = "";

    @Column(name = "popular_time")
    private String popularTime = "";

    @Column(name = "distance")
    private String distance = "";

    @Column(name = "avg_cab_fare")
    private String avgCabFare = "";

    public DestinationEntity() {}

    public DestinationEntity(String id, String name, String icon, String description, String popularTime, String distance, String avgCabFare) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.description = description;
        this.popularTime = popularTime;
        this.distance = distance;
        this.avgCabFare = avgCabFare;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPopularTime() { return popularTime; }
    public void setPopularTime(String popularTime) { this.popularTime = popularTime; }

    public String getDistance() { return distance; }
    public void setDistance(String distance) { this.distance = distance; }

    public String getAvgCabFare() { return avgCabFare; }
    public void setAvgCabFare(String avgCabFare) { this.avgCabFare = avgCabFare; }
}
