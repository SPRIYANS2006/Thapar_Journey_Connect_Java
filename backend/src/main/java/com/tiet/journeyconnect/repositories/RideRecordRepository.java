package com.tiet.journeyconnect.repositories;

import com.tiet.journeyconnect.models.entities.RideRecordEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideRecordRepository extends MongoRepository<RideRecordEntity, String> {
    List<RideRecordEntity> findAllByOrderByCompletedAtDesc();
}
