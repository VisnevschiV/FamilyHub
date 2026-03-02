package com.visnevschi.familyhub.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.visnevschi.familyhub.document.TaskList;

@Repository
public interface TaskListRepository extends MongoRepository<TaskList, String> {

    List<TaskList> findAllByFamilyId(Long familyId);
    long deleteByIdAndFamilyId(String id, Long familyId);
    TaskList findByIdAndFamilyId(String id, Long familyId);
}
