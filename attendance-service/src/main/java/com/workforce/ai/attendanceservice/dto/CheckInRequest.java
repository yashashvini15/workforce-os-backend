package com.workforce.ai.attendanceservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckInRequest {

    @NotNull(message = "Latitude is required")
    private Double latitude;

    @NotNull(message = "Longitute is required")
    private Double longitude;

    private String wifiSsid;

    @NotNull(message = "Device Id is required")
    private String deviceId;
}
