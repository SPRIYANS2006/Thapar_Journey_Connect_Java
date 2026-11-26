package com.tiet.journeyconnect.models.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document(collection = "community_posts")
public class CommunityPostEntity {

    @Id
private String id;
private String communityId;
private String authorId;
private String authorName;
private String authorAvatar = "ST";
private String authorHostel = "";
private String category = "Ride Sharing";
private String title;
private String content;
private String likes = ""; // Comma-separated user IDs who liked
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
