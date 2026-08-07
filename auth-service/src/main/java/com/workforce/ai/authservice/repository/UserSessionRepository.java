package com.workforce.ai.authservice.repository;

import com.workforce.ai.authservice.entity.UserSessions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserSessionRepository extends JpaRepository<UserSessions, UUID> {
    List<UserSessions> findByEmail(String email);

    Optional<UserSessions> findByTokenId(String tokenId);

    @Modifying
    @Transactional
    @Query("delete from UserSessions s where s.email = :email")
    void deleteByEmail(String email);

    @Modifying
    @Transactional
    void deleteByTokenId(String tokenId);

    @Modifying
    @Transactional
    @Query("delete from UserSessions s where s.expiryTime < CURRENT TIMESTAMP ")
    void deleteExpiredSessions();
}
