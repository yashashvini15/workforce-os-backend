package com.workforce.ai.attendanceservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CorrectionRequest {
    private LocalDateTime requestCheckInTime;
    private LocalDateTime requestCheckOutTime;
    private String reason;
}
