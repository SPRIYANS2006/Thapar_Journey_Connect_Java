package com.tiet.journeyconnect.repositories;

import com.tiet.journeyconnect.models.entities.TravelGroupEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TravelGroupRepository extends MongoRepository<TravelGroupEntity, String> {
    List<TravelGroupEntity> findByStatusOrderByDateAscDepartureTimeAsc(String status);
}
