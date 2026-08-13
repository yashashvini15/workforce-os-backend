package com.workforce.ai.authservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "department_id",nullable = false)
    private Department department;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private User manager;

    public Team(String name, Department department, User manager) {
        this.name = name;
        this.department = department;
        this.manager = manager;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }
}
