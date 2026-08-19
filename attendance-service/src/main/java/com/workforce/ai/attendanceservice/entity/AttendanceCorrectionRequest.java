package com.workforce.ai.attendanceservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "attendance_correction_requests")
@Getter
@Setter
@NoArgsConstructor
public class AttendanceCorrectionRequest {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "attendance_id", nullable = false)
    private UUID attendanceId;

    @Column(name = "requested_by",nullable = false)
    private UUID requestBy;

    private LocalDateTime requestedCheckInTime;
    private LocalDateTime requestedCheckOutTime;

    @Column(nullable = false,length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CorrectionStatus status = CorrectionStatus.PENDING;

    private UUID reviewedBy;
    private LocalDateTime reviewedAt;

    @Column(length = 500)
    private String reviewComment;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Version
    private Long version;
}
