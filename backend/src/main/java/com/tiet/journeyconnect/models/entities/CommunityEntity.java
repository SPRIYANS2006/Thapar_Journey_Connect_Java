package com.tiet.journeyconnect.models.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document(collection = "communities")
public class CommunityEntity {

    @Id
private String id;
private String name;
private String state;
private String icon = "🏙️";
private Integer membersCount = 0;
private String distanceFromThapar = "";
private String popularTransport = "";
private String description = "";
private String tags = ""; // Comma-separated tags
private String createdBy;
private Instant createdAt = Instant.now();

    public CommunityEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public Integer getMembersCount() { return membersCount != null ? membersCount : 0; }
    public void setMembersCount(Integer membersCount) { this.membersCount = membersCount; }

    public String getDistanceFromThapar() { return distanceFromThapar; }
    public void setDistanceFromThapar(String distanceFromThapar) { this.distanceFromThapar = distanceFromThapar; }

    public String getPopularTransport() { return popularTransport; }
    public void setPopularTransport(String popularTransport) { this.popularTransport = popularTransport; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTags() { return tags != null ? tags : ""; }
    public void setTags(String tags) { this.tags = tags; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
