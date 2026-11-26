package com.tiet.journeyconnect.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "communities")
public class CommunityEntity {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "icon")
    private String icon = "🏙️";

    @Column(name = "members_count")
    private Integer membersCount = 0;

    @Column(name = "distance_from_thapar")
    private String distanceFromThapar = "";

    @Column(name = "popular_transport")
    private String popularTransport = "";

    @Column(name = "description", length = 1000)
    private String description = "";

    @Column(name = "tags", length = 500)
    private String tags = ""; // Comma-separated tags

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at")
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
