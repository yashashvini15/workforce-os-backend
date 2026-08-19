package com.workforce.ai.attendanceservice.dto;

import com.workforce.ai.attendanceservice.entity.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class AttendanceResponse {
    private UUID id;
    private LocalDate attendanceDate;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private AttendanceStatus status;
    private boolean geodenceVerified;
    private boolean wifiVerified;
}
