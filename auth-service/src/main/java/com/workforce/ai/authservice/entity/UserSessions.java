package com.workforce.ai.authservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "user-session")
public class UserSessions {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false,unique = true)
    private String tokenId;

    @Column(nullable = false)
    private String deviceInfo;

    @Column(nullable = false)
    private LocalDateTime loginTime;

    @Column(nullable = false)
    private LocalDateTime expiryTime;

    public UserSessions(String email, String tokenId, String deviceInfo, LocalDateTime loginTime, LocalDateTime expiryTime) {
        this.email = email;
        this.tokenId = tokenId;
        this.deviceInfo = deviceInfo;
        this.loginTime = loginTime;
        this.expiryTime = expiryTime;
    }
}
