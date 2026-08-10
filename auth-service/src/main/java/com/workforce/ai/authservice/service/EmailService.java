package com.workforce.ai.authservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class EmailService {
    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.from.email}")
    private String fromEmail;

    @Value("${brevo.from.name}")
    private String fromName;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String BREVO_API_URL =
            "https://api.brevo.com/v3/smtp/email";

    public void sendPasswordResetEmail(String toEmail, String token){
        String resetLink = frontendUrl + "/reset-password?token=" + token;
        String body =
                "Hello,\n\n" +
                        "We received a request to reset your password.\n\n" +
                        "Click the link below to reset your password:\n\n" +
                        resetLink +
                        "\n\n" +
                        "This link will expire in 30 minutes.\n\n" +
                        "If you did not request a password reset, please ignore this email.\n\n" +
                        "Regards,\n" +
                        "AI Workforce OS";
        sendEmail(toEmail, "Reset your password - AI Workforce OS", body);
    }

    public void sendEmail(String toEmail, String subject, String bodyText){
        Map<String, Object> payload = Map.of(
                "sender", Map.of(
                        "name", fromName,
                        "email", fromEmail
                ),
                "to", List.of(
                        Map.of(
                                "email", toEmail
                        )
                ),
                "subject", subject,
                "textContent", bodyText
        );

        HttpHeaders headers = new HttpHeaders();

        headers.set("api-key", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload,headers);

        restTemplate.postForEntity(BREVO_API_URL,request,String.class);
    }
}
