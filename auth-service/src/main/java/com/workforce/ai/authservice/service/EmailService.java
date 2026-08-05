package com.workforce.ai.authservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${app.frontent-url}")
    private String frontendUrl;

    public EmailService(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(String toEmail, String token){
        String resetLink = frontendUrl + "/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Reset Your Password - AI Workforce OS");
        message.setText("Click the link below to reset your password:\n\n" + resetLink + "\n\n This link will expire in 30 minutes. If you didn't request this, please ignore this email.");

        mailSender.send(message);
    }
}
