package com.task.projectservice.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

import com.task.user.entity.User;

@Entity
@Table(name = "Project")
public class Project {

    @Id
    @Column(name = "ProjectID")
    private Integer projectId;

    @Column(name = "ProjectName", nullable = false)
    private String projectName;

    @Column(name = "Description")
    private String description;

    @Column(name = "StartDate")
    private LocalDate startDate;

    @Column(name = "EndDate")
    private LocalDate endDate;

 
    @ManyToOne
    @JoinColumn(name = "UserID")
    private User user;


    public Project() {}

    public Project(Integer projectId, String projectName, String description,
                   LocalDate startDate, LocalDate endDate, User user) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.user = user;
    }



    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}