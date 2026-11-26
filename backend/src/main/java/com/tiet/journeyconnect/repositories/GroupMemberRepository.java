package com.tiet.journeyconnect.repositories;

import com.tiet.journeyconnect.models.entities.GroupMemberEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends MongoRepository<GroupMemberEntity, String> {
    List<GroupMemberEntity> findByTargetIdAndGroupTypeOrderByJoinedAtAsc(String targetId, String groupType);
    List<GroupMemberEntity> findByTargetIdOrderByJoinedAtAsc(String targetId);
    List<GroupMemberEntity> findByUserId(String userId);
    Optional<GroupMemberEntity> findByTargetIdAndUserId(String targetId, String userId);
    void deleteByTargetIdAndUserId(String targetId, String userId);
    void deleteByTargetId(String targetId);
}
