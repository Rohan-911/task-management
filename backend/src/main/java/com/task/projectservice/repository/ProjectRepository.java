package com.task.projectservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Integer>{
	public findByUser(Integer userId);
}
