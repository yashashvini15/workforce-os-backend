package com.workforce.ai.attendanceservice.service;

import com.workforce.ai.attendanceservice.dto.AttendanceResponse;
import com.workforce.ai.attendanceservice.dto.CheckInRequest;
import com.workforce.ai.attendanceservice.dto.CheckOutRequest;
import com.workforce.ai.attendanceservice.entity.Attendance;
import com.workforce.ai.attendanceservice.entity.AttendanceStatus;
import com.workforce.ai.attendanceservice.exception.CustomException;
import com.workforce.ai.attendanceservice.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class AttendanceService{

    private final AttendanceRepository attendanceRepository;
    private final GeofenceUtil geofenceUtil;

    @Value("${office.latitude}")
    private double officeLatitude;

    @Value("${office.longitude}")
    private double officeLongitude;

    @Value("${office.geofence.radius-meters}")
    private double geofenceRadiusMeters;

    @Value("${office.wifi.ssid}")
    private String officeWifiSsid;

    @Value("${office.start-time}")
    private String officeStartTimeStr;

    @Value("${office.late-threshold-minutes}")
    private int lateThresholdMinutes;

    public AttendanceService(AttendanceRepository attendanceRepository, GeofenceUtil geofenceUtil) {
        this.attendanceRepository = attendanceRepository;
        this.geofenceUtil = geofenceUtil;
    }


    @Transactional
    public AttendanceResponse checkIn(UUID userId, CheckInRequest request){
        LocalDate today = LocalDate.now();

        attendanceRepository.findByUserIdAndAttendanceDate(userId,today)
                .ifPresent(a->{
                    throw new CustomException("You have already checked in today");
                });

        boolean isGeofenceOk = geofenceUtil.isWithGeofence(
                request.getLatitude(),request.getLongitude(),
                officeLatitude,officeLongitude,geofenceRadiusMeters
        );

        boolean isWifiOk = request.getWifiSsid() != null && request.getWifiSsid().equalsIgnoreCase(officeWifiSsid);

        if (!isGeofenceOk && !isWifiOk){
            throw new CustomException("Check-in failed: You are not within office premises or connect to office Wifi");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalTime officeStartTime = LocalTime.parse(officeStartTimeStr);
        LocalTime cutoffTime = officeStartTime.plusMinutes(lateThresholdMinutes);

        AttendanceStatus status = now.toLocalTime().isAfter(cutoffTime)
                ? AttendanceStatus.LATE: AttendanceStatus.PRESENT;

        Attendance attendance = new Attendance();
        attendance.setUserId(userId);
        attendance.setAttendanceDate(today);
        attendance.setCheckInTime(now);
        attendance.setStatus(status);
        attendance.setCheckInLatitude(request.getLatitude());
        attendance.setCheckInLongitude(request.getLongitude());
        attendance.setWifiSsid(request.getWifiSsid());
        attendance.setDeviceId(request.getDeviceId());
        attendance.setGeofenceVerified(isGeofenceOk);
        attendance.setWifiVerified(isWifiOk);

        attendanceRepository.save(attendance);

        return toResponse(attendance);
    }


    @Transactional
    public AttendanceResponse checkOut(UUID userId, CheckOutRequest request){
        LocalDate today = LocalDate.now();

        Attendance attendance = attendanceRepository.findByUserIdAndAttendanceDate(userId,today)
                .orElseThrow(() -> new CustomException("You have not checked in today"));
        if(attendance.getCheckOutTime() != null){
            throw new CustomException("You have checkout out today");
        }

        attendance.setCheckOutTime(LocalDateTime.now());
        attendanceRepository.save(attendance);

        return toResponse(attendance);
    }


    public List<AttendanceResponse> getMyHistory(UUID userId){
        return attendanceRepository.findByUserIdOrderByAttendanceDateDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<AttendanceResponse> getHistoryInRange(UUID userId, LocalDate start, LocalDate end){
        if(start.isAfter(end)){
            throw new CustomException("Start date cannot be after end date");
        }

        return attendanceRepository.findByUserIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
                userId,start,end)
                .stream()
                .map(this::toResponse)
                .toList();
    }


    private AttendanceResponse toResponse(Attendance a){
        return new AttendanceResponse(a.getId(),a.getAttendanceDate(),a.getCheckInTime(),a.getCheckOutTime(),
                a.getStatus(),a.isGeofenceVerified(),a.isWifiVerified());
    }
}
