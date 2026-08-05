package com.workforce.ai.authservice.controller;

import com.workforce.ai.authservice.dto.AuthResponse;
import com.workforce.ai.authservice.dto.LoginRequest;
import com.workforce.ai.authservice.dto.SignupRequest;
import com.workforce.ai.authservice.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/signup")
    public AuthResponse signup(@RequestBody SignupRequest signupRequest){
        return authService.signup(signupRequest);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest loginRequest){
        return authService.login(loginRequest);
    }
}