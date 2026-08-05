package com.workforce.ai.authservice.repository;

import com.workforce.ai.authservice.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByEmail(String email);
    @Modifying
    @Transactional
    void deleteByExpiryDateBefore(LocalDateTime now);
}
