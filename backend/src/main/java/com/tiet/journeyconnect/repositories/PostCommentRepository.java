package com.tiet.journeyconnect.repositories;

import com.tiet.journeyconnect.models.entities.PostCommentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentRepository extends MongoRepository<PostCommentEntity, String> {
    List<PostCommentEntity> findByPostIdOrderByCreatedAtAsc(String postId);
    void deleteByPostId(String postId);
}
