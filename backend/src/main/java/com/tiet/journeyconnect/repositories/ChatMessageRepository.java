package com.tiet.journeyconnect.repositories;

import com.tiet.journeyconnect.models.entities.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, String> {
    List<ChatMessageEntity> findByGroupIdOrderByCreatedAtAsc(String groupId);
    void deleteByGroupId(String groupId);
}
