package com.visnevschi.familyhub.repository;

import com.visnevschi.familyhub.dbenitity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    boolean existsByEmail(String email);
    Optional<UserAccount> findByEmail(String email);
}