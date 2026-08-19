package com.workforce.ai.attendanceservice.controller;

import com.workforce.ai.attendanceservice.dto.AttendanceResponse;
import com.workforce.ai.attendanceservice.dto.CheckInRequest;
import com.workforce.ai.attendanceservice.dto.CheckOutRequest;
import com.workforce.ai.attendanceservice.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/check-in")
    public AttendanceResponse checkIn(@Valid @RequestBody CheckInRequest request, Authentication authentication){
        UUID userId = extractUserId(authentication);
        return attendanceService.checkIn(userId,request);
    }

    @PostMapping("/check-out")
    public AttendanceResponse checkOut(@RequestBody CheckOutRequest request,Authentication authentication){
        UUID userId = extractUserId(authentication);
        return attendanceService.checkOut(userId,request);
    }

    @GetMapping("/my-history")
    public List<AttendanceResponse> getMyAttendance(Authentication authentication){
        UUID userId = extractUserId(authentication);
        return attendanceService.getMyHistory(userId);
    }

    @GetMapping("/my-history/range")
    public List<AttendanceResponse> getHistoryInRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end ,
            Authentication authentication){

        UUID userId = extractUserId(authentication);
        return attendanceService.getHistoryInRange(userId,start,end);
    }

    private UUID extractUserId(Authentication authentication){
        return UUID.fromString(authentication.getName());
    }
}
