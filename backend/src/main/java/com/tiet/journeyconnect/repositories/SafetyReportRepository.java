package com.tiet.journeyconnect.repositories;

import com.tiet.journeyconnect.models.entities.SafetyReportEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface SafetyReportRepository extends MongoRepository<SafetyReportEntity, String> {
    List<SafetyReportEntity> findByReporterIdOrderByCreatedAtDesc(String reporterId);
    List<SafetyReportEntity> findAllByOrderByCreatedAtDesc();
    long countByReporterIdAndCreatedAtGreaterThanEqual(String reporterId, Instant since);
}
