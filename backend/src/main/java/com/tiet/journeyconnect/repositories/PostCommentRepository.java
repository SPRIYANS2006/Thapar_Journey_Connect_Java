package com.tiet.journeyconnect.repositories;

import com.tiet.journeyconnect.models.entities.PostCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostCommentEntity, String> {
    List<PostCommentEntity> findByPostIdOrderByCreatedAtAsc(String postId);
    void deleteByPostId(String postId);
}
