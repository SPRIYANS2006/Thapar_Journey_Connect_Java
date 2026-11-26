package com.tiet.journeyconnect.models.entities;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "community_posts")
public class CommunityPostEntity {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "community_id", nullable = false)
    private String communityId;

    @Column(name = "author_id", nullable = false)
    private String authorId;

    @Column(name = "author_name", nullable = false)
    private String authorName;

    @Column(name = "author_avatar")
    private String authorAvatar = "ST";

    @Column(name = "author_hostel")
    private String authorHostel = "";

    @Column(name = "category")
    private String category = "Ride Sharing";

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", length = 3000, nullable = false)
    private String content;

    @Column(name = "likes", length = 3000)
    private String likes = ""; // Comma-separated user IDs who liked

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    public CommunityPostEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCommunityId() { return communityId; }
    public void setCommunityId(String communityId) { this.communityId = communityId; }

    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getAuthorAvatar() { return authorAvatar != null ? authorAvatar : "ST"; }
    public void setAuthorAvatar(String authorAvatar) { this.authorAvatar = authorAvatar; }

    public String getAuthorHostel() { return authorHostel != null ? authorHostel : ""; }
    public void setAuthorHostel(String authorHostel) { this.authorHostel = authorHostel; }

    public String getCategory() { return category != null ? category : "Ride Sharing"; }
    public void setCategory(String category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getLikes() { return likes != null ? likes : ""; }
    public void setLikes(String likes) { this.likes = likes; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
