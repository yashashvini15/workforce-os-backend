package com.workforce.ai.authservice.controller;

import com.workforce.ai.authservice.dto.DepartmentRequest;
import com.workforce.ai.authservice.dto.TeamRequest;
import com.workforce.ai.authservice.dto.TeamResponse;
import com.workforce.ai.authservice.dto.UserResponse;
import com.workforce.ai.authservice.entity.Department;
import com.workforce.ai.authservice.service.OrganizationService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/organization")
public class OrganizationController {
    private final OrganizationService organizationService;

    @PreAuthorize("hasAnyRole('HR_ADMIN','SUPER_ADMIN')")
    @PostMapping("/departments")
    public Department createDepartment(@RequestBody DepartmentRequest request){
        return organizationService.createDepartment(request.getName());
    }

    @PreAuthorize("hasAnyRole('HR_ADMIN','SUPER_ADMIN','MANAGER')")
    @GetMapping("/departments")
    public List<Department> getAllDepartments(){
        return organizationService.getAllDepartments();
    }

    @PreAuthorize("hasAnyRole('HR_ADMIN','SUPER_ADMIN')")
    @PostMapping("/teams")
    public TeamResponse createTeam(@RequestBody TeamRequest request){
        return organizationService.createTeam(request);
    }

    @PreAuthorize("hasAnyRole('HR_ADMIN','SUPER_ADMIN','MANAGER')")
    @GetMapping("/teams")
    public List<TeamResponse> getAllTeams(){
        return organizationService.getAllTeams();
    }

    @PreAuthorize("hasAnyRole('HR_ADMIN','SUPER_ADMIN')")
    @PutMapping("/users/{userId}/assign-team/{teamId}")
    public UserResponse assignUserToTeam(@PathVariable UUID userId, @PathVariable UUID teamId){
        return organizationService.assignUserToTeam(userId,teamId);
    }

    @PreAuthorize("hasAnyRole('MANAGER')")
    @GetMapping("/my-team")
    public List<UserResponse> getMyTeam(Authentication authentication){
        return organizationService.getMyTeamMembers(authentication.getName());
    }

}
