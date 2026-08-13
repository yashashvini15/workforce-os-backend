package com.workforce.ai.authservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TeamRequest {
    private String name;
    private UUID departmentId;
    private UUID managerId;
}
