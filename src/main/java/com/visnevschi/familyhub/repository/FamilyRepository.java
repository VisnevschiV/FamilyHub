package com.visnevschi.familyhub.repository;

import com.visnevschi.familyhub.dbenitity.Family;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FamilyRepository extends JpaRepository<Family, Long> {

    Optional<Family> findByJoinCode(String joinCode);
}
