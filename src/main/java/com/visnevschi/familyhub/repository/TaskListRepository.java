package com.visnevschi.familyhub.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.visnevschi.familyhub.document.TaskList;

@Repository
public interface TaskListRepository extends MongoRepository<TaskList, String> {

    List<TaskList> findAllByFamilyId(Long familyId);
    List<TaskList> findAllByFamilyIdAndParticipantsContains(Long familyId, Long participantId);

    @Query("{ 'family_id': ?0, $or: [ { 'participants': { $exists: false } }, { 'participants': { $size: 0 } }, { 'participants': ?1 } ] }")
    List<TaskList> findVisibleByFamilyIdAndPersonaId(Long familyId, Long personaId);

    @Query("{ '_id': ?0, 'family_id': ?1, $or: [ { 'participants': { $exists: false } }, { 'participants': { $size: 0 } }, { 'participants': ?2 } ] }")
    TaskList findVisibleByIdAndFamilyIdAndPersonaId(String id, Long familyId, Long personaId);

    long deleteByIdAndFamilyId(String id, Long familyId);
    TaskList findByIdAndFamilyId(String id, Long familyId);
}
