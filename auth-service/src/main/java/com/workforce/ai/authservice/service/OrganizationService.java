package com.workforce.ai.authservice.service;

import com.workforce.ai.authservice.dto.TeamRequest;
import com.workforce.ai.authservice.dto.TeamResponse;
import com.workforce.ai.authservice.dto.UserResponse;
import com.workforce.ai.authservice.entity.Department;
import com.workforce.ai.authservice.entity.Team;
import com.workforce.ai.authservice.entity.User;
import com.workforce.ai.authservice.exception.CustomException;
import com.workforce.ai.authservice.repository.DepartmentRepository;
import com.workforce.ai.authservice.repository.TeamRepository;
import com.workforce.ai.authservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrganizationService {
    private final DepartmentRepository departmentRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    @Transactional
    public Department createDepartment(String name){
        Department department = new Department(name);
        return departmentRepository.save(department);
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Transactional
    public TeamResponse createTeam(TeamRequest teamRequest){
        Department department = departmentRepository.findById(teamRequest.getDepartmentId())
                .orElseThrow(()-> new CustomException("Department not found"));

        User manager = null;

        if(teamRequest.getManagerId() != null){
            manager = userRepository.findById(teamRequest.getManagerId())
                    .orElseThrow(()-> new CustomException("Manager not found"));
        }

        Team team = new Team(teamRequest.getName(), department, manager);
        teamRepository.save(team);

        return toTeamResponse(team);
    }

    public List<TeamResponse> getAllTeams(){
        return teamRepository.findAll().stream()
                .map(this::toTeamResponse)
                .toList();
    }

    @Transactional
    public UserResponse assignUserToTeam(UUID userId, UUID teamID){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new CustomException("User not found"));

        Team team =  teamRepository.findById(teamID)
                .orElseThrow(()-> new CustomException("Team not found"));

        user.setTeam(team);
        userRepository.save(user);

        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole().name());
    }

    public List<UserResponse> getMyTeamMembers(String managerEmail){
        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(()-> new CustomException("Manager not found"));

        List<Team> managedTeams = teamRepository.findByManagerId(manager.getId());

        return managedTeams.stream()
                .flatMap(team -> userRepository.findByTeamId(team.getId()).stream())
                .map(u -> new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getRole().name()))
                .toList();
    }

    private TeamResponse toTeamResponse(Team team){
        return new TeamResponse(
                team.getId(),
                team.getName(),
                team.getDepartment().getName(),
                team.getManager() != null? team.getManager().getName() : "Not Assigned"
        );
    }

}
