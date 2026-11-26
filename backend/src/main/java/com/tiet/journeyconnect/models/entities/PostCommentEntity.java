package com.tiet.journeyconnect.models.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document(collection = "post_comments")
public class PostCommentEntity {

    @Id
private String id;
private String postId;
private String authorId;
private String authorName;
private String authorAvatar = "ST";
private String authorHostel = "";
private String text;
private Instant createdAt = Instant.now();

    public PostCommentEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }

    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getAuthorAvatar() { return authorAvatar != null ? authorAvatar : "ST"; }
    public void setAuthorAvatar(String authorAvatar) { this.authorAvatar = authorAvatar; }

    public String getAuthorHostel() { return authorHostel != null ? authorHostel : ""; }
    public void setAuthorHostel(String authorHostel) { this.authorHostel = authorHostel; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
