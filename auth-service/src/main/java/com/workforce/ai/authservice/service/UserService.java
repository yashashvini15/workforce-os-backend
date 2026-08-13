package com.workforce.ai.authservice.service;

import com.workforce.ai.authservice.dto.UserResponse;
import com.workforce.ai.authservice.entity.Role;
import com.workforce.ai.authservice.entity.User;
import com.workforce.ai.authservice.exception.CustomException;
import com.workforce.ai.authservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository=userRepository;
    }

    public UserResponse getMyProfile(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new CustomException("User Not Found"));
        return toResponse(user);
    }

    public List<UserResponse> getAllUsers(){
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponse updateUserRole(UUID userId, String newRole,String requesterEmail){
        User user = userRepository.findById(userId)
                .orElseThrow(()->new CustomException("User Not Found"));

        if(user.getEmail().equalsIgnoreCase(requesterEmail)){
            throw new CustomException("You cannot change your own role");
        }

        Role role;
        try{
            role = Role.valueOf(newRole.toUpperCase());
        }catch (IllegalArgumentException e){
            throw new CustomException("Invalid role: "+newRole);
        }

        if(user.getRole() == Role.SUPER_ADMIN && role != Role.SUPER_ADMIN){
            long superAdminCount = userRepository.countByRole(Role.SUPER_ADMIN);
            if(superAdminCount <= 1){
                throw new CustomException("Cannot demote the last SUPER_ADMIN");
            }
        }

        user.setRole(role);
        userRepository.save(user);
        return toResponse(user);
    }


    private UserResponse toResponse(User user){
        return new UserResponse(user.getId(), user.getName(), user.getEmail(),user.getRole().name());
    }
}
