package com.workforce.ai.attendanceservice.dto;

import com.workforce.ai.attendanceservice.entity.CorrectionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class CorrectionResponse {
    private UUID id;
    private UUID attendanceId;
    private UUID requestedBy;
    private LocalDateTime reqquestedCheckInTime;
    private LocalDateTime getReqquestedCheckOutTime;
    private String reason;
    private CorrectionStatus status;
    private String reviewComment;
    private LocalDateTime createdAt;
}
