package com.workforce.ai.attendanceservice.repository;

import com.workforce.ai.attendanceservice.entity.AttendanceCorrectionRequest;
import com.workforce.ai.attendanceservice.entity.CorrectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AttendanceCorrectionRepository extends JpaRepository<AttendanceCorrectionRequest, UUID> {

    List<AttendanceCorrectionRequest> findByRequestByOrderByCreatedAtDesc(UUID requestedBy);
    List<AttendanceCorrectionRequest> findByStatusOrderByCreatedAtDesc(CorrectionStatus status);
}
