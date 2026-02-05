package com.visnevschi.familyhub.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.visnevschi.familyhub.dbenitity.UserAccount;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    boolean existsByEmail(String email);
    Optional<UserAccount> findByEmail(String email);
    Optional<UserAccount> findByRefreshToken(String refreshToken);
}