package com.task.projectservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.task.projectservice.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Integer>{
	public Project findByUser(Integer userId);
}
