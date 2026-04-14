package com.task.projectservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.task.projectservice.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Integer>{
	public List<Project> findByUser_UserId(Integer userId);
	long countByUser_UserId(Integer userId);
}
