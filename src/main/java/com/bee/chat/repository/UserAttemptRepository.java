package com.bee.chat.repository;

import com.bee.chat.model.UserAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAttemptRepository extends JpaRepository<UserAttempt, Long> {
    Optional<UserAttempt> findByUsername(String username);
}
