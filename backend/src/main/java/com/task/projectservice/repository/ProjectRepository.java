package com.task.projectservice.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.task.projectservice.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Integer>{
	public List<Project> findByUser_UserId(Integer userId);
	public long countByUser_UserId(Integer userId);
	public List<Project> findByUserIsNull();
	public List<Project> findByEndDateGreaterThanEqual(LocalDate date);
    public List<Project> findByEndDateLessThan(LocalDate date);
}
