package com.workforce.ai.authservice.service;

import com.workforce.ai.authservice.dto.SessionResponse;
import com.workforce.ai.authservice.entity.UserSessions;
import com.workforce.ai.authservice.repository.UserSessionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class SessionService {
    private final UserSessionRepository userSessionRepository;

    @Transactional
    public void createSession(String email, String tokenId, String deviceInfo, LocalDateTime expiryTime){
        UserSessions sessions = new UserSessions(email,tokenId,deviceInfo,LocalDateTime.now(),expiryTime);
        userSessionRepository.save(sessions);
    }

    public List<SessionResponse> getActiveSessions(String email){
        return userSessionRepository.findByEmail(email).stream().map(
                s-> new SessionResponse(s.getId(),s.getDeviceInfo(),s.getLoginTime(),s.getExpiryTime()))
                .toList();
    }

    @Transactional
    public void logoutAllDevices(String email){
        userSessionRepository.deleteByEmail(email);
    }

    @Transactional
    public void logoutSingleSession(String tokenId){
        userSessionRepository.deleteByTokenId(tokenId);
    }

    public boolean isSessionValid(String tokenId){
        return userSessionRepository.findByTokenId(tokenId).isPresent();
    }
}
