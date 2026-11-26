package com.tiet.journeyconnect.repositories;

import com.tiet.journeyconnect.models.entities.CityRideEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRideRepository extends JpaRepository<CityRideEntity, String> {
    Optional<CityRideEntity> findBySlotKey(String slotKey);
    List<CityRideEntity> findByCommunityIdAndDateAndStatusIn(String communityId, String date, List<String> statuses);
    List<CityRideEntity> findByCommunityIdAndStatusIn(String communityId, List<String> statuses);
}
