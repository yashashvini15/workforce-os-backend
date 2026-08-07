package com.workforce.ai.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class SessionResponse {
    private UUID id;
    private String deviceInfo;
    private LocalDateTime loginTime;
    private LocalDateTime expiryTime;
}
