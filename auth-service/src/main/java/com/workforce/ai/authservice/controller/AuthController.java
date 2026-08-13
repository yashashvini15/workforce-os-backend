package com.workforce.ai.authservice.controller;

import com.workforce.ai.authservice.dto.*;
import com.workforce.ai.authservice.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/signup")
    public String signup(@RequestBody SignupRequest signupRequest){
        return authService.signup(signupRequest);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest){
        return authService.login(loginRequest);
    }

    @PostMapping("/verify-otp")
    public AuthResponse verifyOtp(@RequestBody VerifyOtpRequest request, HttpServletRequest httpRequest){
        String deviceInfo = httpRequest.getHeader("User-Agent");
        return authService.verifyLoginOtp(request.getEmail(), request.getOtp(), deviceInfo);
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestBody ForgotPasswordRequest request){
        authService.forgotPassword(request.getEmail());
        return "If this email exists, a reset link been sent";
    }
    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody ResetPasswordRequest request){
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return "Password reset successful. You can now login with your new password.";
    }
}