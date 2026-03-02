package com.visnevschi.familyhub.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.visnevschi.familyhub.dbenitity.Persona;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {
    Optional<Persona> findByUserAccountId(Long userAccountId);
    Optional<Persona> findByUserAccountEmail(String email);
    boolean existsByUserAccountId(Long userAccountId);
    long countByFamilyId(Long familyId);
}
