package com.workforce.ai.authservice.service;

import com.workforce.ai.authservice.entity.OtpToken;
import com.workforce.ai.authservice.exception.CustomException;
import com.workforce.ai.authservice.repository.OtpTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class OtpService {
    private final OtpTokenRepository otpTokenRepository;
    private final JavaMailSender mailSender;

    @Transactional
    public void generateAndSendOtp(String email){
        otpTokenRepository.deleteByEmail(email);

        String otp = String.valueOf(new SecureRandom().nextInt(900000) + 100000);

        OtpToken otpToken = new OtpToken(email, otp, LocalDateTime.now().plusMinutes(5));

        otpTokenRepository.save(otpToken);
        sendOtpEmail(email, otp);
    }

    @Transactional
    public void verifyOtp(String email, String otp){
        OtpToken otpToken = otpTokenRepository.findByEmailAndOtp(email, otp)
                .orElseThrow(()-> new CustomException("Invalid OTP"));

        if(otpToken.getExpiryDate().isBefore(LocalDateTime.now())){
            otpTokenRepository.deleteByEmail(email);
            throw new CustomException("OTP has expired. Please Try again");
        }

        otpTokenRepository.deleteByEmail(email);
    }

    private void sendOtpEmail(String toEmail,String otp){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Your Login OTP - AI Workforce OS");
        message.setText("Your OTP for login is: "+otp+"\n\nThis OTP will expire in 5 minutes.");
        mailSender.send(message);
    }
}
