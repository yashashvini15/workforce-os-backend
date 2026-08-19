package com.workforce.ai.attendanceservice.service;

import com.workforce.ai.attendanceservice.dto.CorrectionRequest;
import com.workforce.ai.attendanceservice.dto.CorrectionResponse;
import com.workforce.ai.attendanceservice.dto.CorrectionReview;
import com.workforce.ai.attendanceservice.entity.Attendance;
import com.workforce.ai.attendanceservice.entity.AttendanceCorrectionRequest;
import com.workforce.ai.attendanceservice.entity.CorrectionStatus;
import com.workforce.ai.attendanceservice.exception.CustomException;
import com.workforce.ai.attendanceservice.repository.AttendanceCorrectionRepository;
import com.workforce.ai.attendanceservice.repository.AttendanceRepository;
import lombok.AllArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AttendanceCorrectionService {
    private final AttendanceCorrectionRepository attendanceCorrectionRepository;
    private final AttendanceRepository attendanceRepository;

    @Transactional
    public CorrectionResponse submitRequest(UUID userId, UUID attendanceId, CorrectionRequest dto){
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(()-> new CustomException("Attendance record not found"));

        if(!attendance.getUserId().equals(userId)){
            throw new CustomException("You can only request correction for your own attendance");
        }

        if(dto.getRequestCheckInTime() == null && dto.getRequestCheckOutTime() == null){
            throw new CustomException("At least one of check-in or check-out time must be provided");
        }

        if (dto.getReason() == null || dto.getReason().isBlank()) {
            throw new CustomException("Reason is required for correction request");
        }

        AttendanceCorrectionRequest request = new AttendanceCorrectionRequest();
        request.setAttendanceId(attendanceId);
        request.setRequestBy(userId);
        request.setRequestedCheckInTime(dto.getRequestCheckInTime());
        request.setRequestedCheckOutTime(dto.getRequestCheckOutTime());
        request.setReason(dto.getReason());
        request.setStatus(CorrectionStatus.PENDING);

        attendanceCorrectionRepository.save(request);

        return toResponse(request);
    }

    public List<CorrectionResponse> getMyRequests(UUID userId) {
        return attendanceCorrectionRepository.findByRequestByOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<CorrectionResponse> getPendingRequests() {
        return attendanceCorrectionRepository.findByStatusOrderByCreatedAtDesc(CorrectionStatus.PENDING)
                .stream()
                .map(this::toResponse)
                .toList();
    }
    
    @Transactional
    public CorrectionResponse approveRequest(UUID requestId, UUID reviewerId, CorrectionReview dto) {
        try {
            AttendanceCorrectionRequest request = attendanceCorrectionRepository.findById(requestId)
                    .orElseThrow(() -> new CustomException("Correction request not found"));

            if (request.getStatus() != CorrectionStatus.PENDING) {
                throw new CustomException("This request has already been reviewed");
            }

            Attendance attendance = attendanceRepository.findById(request.getAttendanceId())
                    .orElseThrow(() -> new CustomException("Attendance record not found"));

            if (request.getRequestedCheckInTime() != null) {
                attendance.setCheckInTime(request.getRequestedCheckInTime());
            }
            if (request.getRequestedCheckOutTime() != null) {
                attendance.setCheckOutTime(request.getRequestedCheckOutTime());
            }
            attendanceRepository.save(attendance);

            request.setStatus(CorrectionStatus.APPROVED);
            request.setReviewedBy(reviewerId);
            request.setReviewedAt(LocalDateTime.now());
            request.setReviewComment(dto.getReviewComment());
            attendanceCorrectionRepository.save(request);

            return toResponse(request);

        } catch (ObjectOptimisticLockingFailureException e) {
            throw new CustomException("This request was just reviewed by someone else. Please refresh.");
        }
    }

    @Transactional
    public CorrectionResponse rejectRequest(UUID requestId, UUID reviewerId, CorrectionReview dto) {

        AttendanceCorrectionRequest request = attendanceCorrectionRepository.findById(requestId)
                .orElseThrow(() -> new CustomException("Correction request not found"));

        if (request.getStatus() != CorrectionStatus.PENDING) {
            throw new CustomException("This request has already been reviewed");
        }

        request.setStatus(CorrectionStatus.REJECTED);
        request.setReviewedBy(reviewerId);
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewComment(dto.getReviewComment());
        attendanceCorrectionRepository.save(request);

        return toResponse(request);
    }

    private CorrectionResponse toResponse(AttendanceCorrectionRequest r) {
        return new CorrectionResponse(
                r.getId(), r.getAttendanceId(), r.getRequestBy(),
                r.getRequestedCheckInTime(), r.getRequestedCheckOutTime(),
                r.getReason(), r.getStatus(), r.getReviewComment(), r.getCreatedAt());
    }
}
