package com.tiet.journeyconnect.repositories;

import com.tiet.journeyconnect.models.entities.CommunityEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityRepository extends MongoRepository<CommunityEntity, String> {
    List<CommunityEntity> findAllByOrderByMembersCountDescNameAsc();
    Optional<CommunityEntity> findByNameIgnoreCaseAndStateIgnoreCase(String name, String state);
}
