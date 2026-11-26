package com.tiet.journeyconnect.repositories;

import com.tiet.journeyconnect.models.entities.DestinationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DestinationRepository extends JpaRepository<DestinationEntity, String> {
    List<DestinationEntity> findAllByOrderByIdAsc();
}
