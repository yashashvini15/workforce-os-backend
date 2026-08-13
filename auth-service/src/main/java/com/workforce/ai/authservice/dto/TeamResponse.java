package com.workforce.ai.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class TeamResponse {
    private UUID id;
    private String name;
    private String departmentName;
    private String managerName;
}
