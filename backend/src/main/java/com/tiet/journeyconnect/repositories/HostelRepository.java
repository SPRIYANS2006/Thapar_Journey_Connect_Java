package com.tiet.journeyconnect.repositories;

import com.tiet.journeyconnect.models.entities.HostelEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HostelRepository extends MongoRepository<HostelEntity, String> {
    List<HostelEntity> findAllByOrderByClusterAscNameAsc();
}
