package com.tiet.journeyconnect.repositories;

import com.tiet.journeyconnect.models.entities.RideRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideRecordRepository extends JpaRepository<RideRecordEntity, String> {
    List<RideRecordEntity> findAllByOrderByCompletedAtDesc();
}
