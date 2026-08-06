package com.workforce.ai.authservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "otp_tokens")
public class OtpToken {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String otp;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    public OtpToken(String email, String otp, LocalDateTime expiryDate) {
        this.email = email;
        this.otp = otp;
        this.expiryDate = expiryDate;
    }
}
