package com.visnevschi.familyhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.visnevschi.familyhub.dbenitity.Family;

@Repository
public interface FamilyRepository extends JpaRepository<Family, Long> {
}
