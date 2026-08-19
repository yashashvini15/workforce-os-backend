package com.workforce.ai.attendanceservice.controller;

import com.workforce.ai.attendanceservice.dto.CorrectionRequest;
import com.workforce.ai.attendanceservice.dto.CorrectionResponse;
import com.workforce.ai.attendanceservice.dto.CorrectionReview;
import com.workforce.ai.attendanceservice.service.AttendanceCorrectionService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/attendance/corrections")
@AllArgsConstructor
public class AttendanceCorrectionController {

    private final AttendanceCorrectionService correctionService;

    @PostMapping("/{attendanceId}")
    public CorrectionResponse submitRequest(@PathVariable UUID attendanceId,
                                            @RequestBody CorrectionRequest dto,
                                            Authentication authentication){
        UUID userId = UUID.fromString(authentication.getName());
        return correctionService.submitRequest(userId,attendanceId,dto);
    }

    @GetMapping("/my-request")
    public List<CorrectionResponse> getMyResquests(Authentication authentication){
        UUID userId = UUID.fromString(authentication.getName());
        return correctionService.getMyRequests(userId);
    }

    @PreAuthorize("hasAnyRole('MANAGER','HR_ADMIN','SUPER_ADMIN')")
    @GetMapping("/pending")
    public List<CorrectionResponse> getPendingRequests(){
        return correctionService.getPendingRequests();
    }

    @PreAuthorize("hasAnyRole('MANAGER','HR_ADMIN','SUPER_ADMIN')")
    @PutMapping("/{requestId}/approve")
    public CorrectionResponse approveRequest(@PathVariable UUID requestId,
                                             @RequestBody CorrectionReview dto,
                                             Authentication authentication){
        UUID reviewerId = UUID.fromString(authentication.getName());
        return correctionService.approveRequest(requestId,reviewerId,dto);
    }

    @PreAuthorize("hasAnyRole('MANAGER','HR_ADMIN','SUPER_ADMIN')")
    @PutMapping("/{requestId}/reject")
    public CorrectionResponse rejectRequest(@PathVariable UUID requestId,
                                            @RequestBody CorrectionReview dto,
                                            Authentication authentication){
        UUID reviewerId = UUID.fromString(authentication.getName());
        return correctionService.rejectRequest(requestId,reviewerId,dto);
    }
}
