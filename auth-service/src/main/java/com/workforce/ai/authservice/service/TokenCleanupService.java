package com.workforce.ai.authservice.service;

import com.workforce.ai.authservice.repository.PasswordResetTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TokenCleanupService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public TokenCleanupService(PasswordResetTokenRepository passwordResetTokenRepository) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void purgeExpiredTokens(){
        passwordResetTokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
        System.out.println("Expired password reset tokens cleaned up successfully.");
    }
}
