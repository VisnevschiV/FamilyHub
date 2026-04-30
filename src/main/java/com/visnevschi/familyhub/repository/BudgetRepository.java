package com.visnevschi.familyhub.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.visnevschi.familyhub.document.Budget;

@Repository
public interface BudgetRepository extends MongoRepository<Budget, String> {
    Budget findByFamilyId(Long familyId);
    List<Budget> findAllByFamilyId(Long familyId);
}
