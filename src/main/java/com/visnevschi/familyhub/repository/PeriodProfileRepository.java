package com.visnevschi.familyhub.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.visnevschi.familyhub.document.PeriodProfile;

@Repository
public interface PeriodProfileRepository extends MongoRepository<PeriodProfile, Long> {

    List<PeriodProfile> findByFamilyId(Long familyId);
}
