package com.workforce.ai.authservice.repository;

import com.workforce.ai.authservice.entity.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface OtpTokenRepository extends JpaRepository<OtpToken, UUID> {
    Optional<OtpToken> findByEmailAndOtp(String email, String otp);

    @Modifying
    @Transactional
    @Query("DELETE FROM OtpToken o WHERE o.email = :email")
    void deleteByEmail(String email);

    @Modifying
    @Transactional
    @Query("delete from OtpToken o where o.expiryDate < current timestamp ")
    void deleteExpiredTokens();
}
