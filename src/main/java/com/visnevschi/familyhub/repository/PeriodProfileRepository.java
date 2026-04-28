package com.visnevschi.familyhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.visnevschi.familyhub.dbenitity.PeriodProfile;

@Repository
public interface PeriodProfileRepository extends JpaRepository<PeriodProfile, Long> {
}
