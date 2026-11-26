package com.tiet.journeyconnect.repositories;

import com.tiet.journeyconnect.models.entities.DestinationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DestinationRepository extends MongoRepository<DestinationEntity, String> {
    List<DestinationEntity> findAllByOrderByIdAsc();
}
