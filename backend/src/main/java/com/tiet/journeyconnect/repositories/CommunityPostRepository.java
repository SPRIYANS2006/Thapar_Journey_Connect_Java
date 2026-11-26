package com.tiet.journeyconnect.repositories;

import com.tiet.journeyconnect.models.entities.CommunityPostEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityPostRepository extends MongoRepository<CommunityPostEntity, String> {
    List<CommunityPostEntity> findByCommunityIdOrderByCreatedAtDesc(String communityId);
}
