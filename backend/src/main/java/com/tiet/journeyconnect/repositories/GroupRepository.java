package com.tiet.journeyconnect.repositories;

import com.tiet.journeyconnect.models.entities.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<GroupEntity, String> {
    Optional<GroupEntity> findBySlotKey(String slotKey);
    List<GroupEntity> findByDestinationIdAndDateAndStatusIn(String destinationId, String date, List<String> statuses);
    long countByStatusIn(List<String> statuses);

    @Query("SELECT MAX(CAST(g.id AS integer)) FROM GroupEntity g WHERE g.id NOT LIKE '%[^0-9]%'")
    Integer findMaxNumericId();
}
