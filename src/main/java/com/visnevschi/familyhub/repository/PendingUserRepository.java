package com.visnevschi.familyhub.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.visnevschi.familyhub.dbenitity.PendingUser;
import com.visnevschi.familyhub.utils.GeneratedCodeRepo;


@Repository
public interface PendingUserRepository extends JpaRepository<PendingUser, Long>, GeneratedCodeRepo<PendingUser> {

    boolean existsByEmail(String email);
    Optional<PendingUser> findByEmail(String email);

}
