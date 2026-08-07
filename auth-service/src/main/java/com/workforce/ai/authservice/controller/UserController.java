package com.workforce.ai.authservice.controller;

import com.workforce.ai.authservice.dto.SessionResponse;
import com.workforce.ai.authservice.dto.UpdateRoleRequest;
import com.workforce.ai.authservice.dto.UserResponse;
import com.workforce.ai.authservice.service.SessionService;
import com.workforce.ai.authservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    private SessionService sessionService;

    public UserController(UserService userService){
        this.userService=userService;
    }

    @GetMapping("/profile")
    public UserResponse getMyProfile(Authentication authentication){
        String email = authentication.getName();
        return userService.getMyProfile(email);
    }

    @PreAuthorize("hasAnyRole('HR_ADMIN','SUPER_ADMIN')")
    @GetMapping
    public List<UserResponse> getAllUsers(){
        return userService.getAllUsers();
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    @PutMapping("/{id}/role")
    public UserResponse updateUserRole(@PathVariable UUID id, @RequestBody UpdateRoleRequest request){
        return userService.updateUserRole(id,request.getRole());
    }

    @GetMapping("/sessions")
    public List<SessionResponse> getMySessions(Authentication authentication){
        return sessionService.getActiveSessions(authentication.getName());
    }

    @DeleteMapping("/sessions/logout-all")
    public String logoutAllDevices(Authentication authentication){
        sessionService.logoutAllDevices(authentication.getName());
        return "Logged out from all devices successfully";
    }
}
