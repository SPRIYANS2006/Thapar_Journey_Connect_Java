package com.tiet.journeyconnect.repositories;

import com.tiet.journeyconnect.models.entities.HostelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HostelRepository extends JpaRepository<HostelEntity, String> {
    List<HostelEntity> findAllByOrderByClusterAscNameAsc();
}
