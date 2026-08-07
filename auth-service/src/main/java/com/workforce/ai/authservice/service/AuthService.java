package com.workforce.ai.authservice.service;

import com.workforce.ai.authservice.dto.AuthResponse;
import com.workforce.ai.authservice.dto.LoginRequest;
import com.workforce.ai.authservice.dto.SignupRequest;
import com.workforce.ai.authservice.entity.PasswordResetToken;
import com.workforce.ai.authservice.entity.User;
import com.workforce.ai.authservice.exception.CustomException;
import com.workforce.ai.authservice.repository.PasswordResetTokenRepository;
import com.workforce.ai.authservice.repository.UserRepository;
import com.workforce.ai.authservice.security.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.TokenStreamFactory;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    private final OtpService otpService;
    private final SessionService sessionService;

    public AuthResponse signup(SignupRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new CustomException("Email already Registered, Please login again.");
        }

        if(request.getPassword() == null || request.getPassword().isBlank()){
            throw new CustomException("Password is required");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token, user.getRole().name());
    }

    public String login(LoginRequest request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException("Invalid email or password"));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new CustomException("Invalid email or password");
        }

        otpService.generateAndSendOtp(user.getEmail());
        return "OTP sent to your registered email. Please verify to complete login.";
    }

    @Transactional
    public void forgotPassword(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new CustomException("No account found with this email"));

        passwordResetTokenRepository.deleteByEmail(email);

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken(token, email, LocalDateTime.now().plusMinutes(30));

        passwordResetTokenRepository.save(resetToken);

        emailService.sendPasswordResetEmail(email,token);
    }

    @Transactional
    public void resetPassword(String token,String newPassword){
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(()-> new CustomException("Invalid or Expired reset link"));

        if(resetToken.getExpiryDate().isBefore(LocalDateTime.now())){
            throw new CustomException("Reset link has expired. Please request a new one.");
        }

        User user = userRepository.findByEmail(resetToken.getEmail())
                .orElseThrow(()-> new CustomException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);

        passwordResetTokenRepository.deleteByEmail(resetToken.getEmail());
    }

    @Transactional
    public AuthResponse verifyLoginOtp(String email, String otp,String deviceInfo){
        otpService.verifyOtp(email,otp);

        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new CustomException("User not found"));

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        String tokenId = jwtUtil.extractTokenId(token);

        sessionService.createSession(user.getEmail(),tokenId , deviceInfo , LocalDateTime.now().plusHours(24));
        return new AuthResponse(token,user.getRole().name());
    }
}
