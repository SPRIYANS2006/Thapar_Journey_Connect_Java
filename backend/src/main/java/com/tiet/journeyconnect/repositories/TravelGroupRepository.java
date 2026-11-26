package com.tiet.journeyconnect.repositories;

import com.tiet.journeyconnect.models.entities.TravelGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TravelGroupRepository extends JpaRepository<TravelGroupEntity, String> {
    List<TravelGroupEntity> findByStatusOrderByDateAscDepartureTimeAsc(String status);
}
