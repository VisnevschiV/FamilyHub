package com.visnevschi.familyhub.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.visnevschi.familyhub.dbenitity.FamilyInvite;
import com.visnevschi.familyhub.utils.GeneratedCodeRepo;

@Repository
public interface FamilyInviteRepository extends JpaRepository<FamilyInvite, Long>, GeneratedCodeRepo<FamilyInvite> {
    Optional<FamilyInvite> findByCode(String code);
    void deleteByFamilyId(Long familyId);
}
