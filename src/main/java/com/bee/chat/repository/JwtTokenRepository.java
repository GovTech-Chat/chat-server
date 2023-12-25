package com.bee.chat.repository;

import com.bee.chat.model.JwtToken;
import com.bee.chat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JwtTokenRepository extends JpaRepository<JwtToken, Long> {
    Optional<JwtToken> findByRefreshToken(String token);

    Optional<JwtToken> findByAccessToken(String token);

    Optional<JwtToken> findByUser(User user);

    @Modifying
    int deleteByUser(User user);
}
