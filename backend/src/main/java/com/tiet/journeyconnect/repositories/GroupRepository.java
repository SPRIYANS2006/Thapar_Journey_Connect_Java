package com.tiet.journeyconnect.repositories;

import com.tiet.journeyconnect.models.entities.GroupEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends MongoRepository<GroupEntity, String> {
    Optional<GroupEntity> findBySlotKey(String slotKey);
    List<GroupEntity> findByDestinationIdAndDateAndStatusIn(String destinationId, String date, List<String> statuses);
    long countByStatusIn(List<String> statuses);

    default Integer findMaxNumericId() {
        return findAll().stream()
                .map(GroupEntity::getId)
                .filter(id -> id != null && id.matches("\\d+"))
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0);
    }
}
