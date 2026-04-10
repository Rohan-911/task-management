package com.task.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.task.entity.Task;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {

    List<Task> findByUserID(Integer userID);

    List<Task> findByProjectID(Integer projectID);
    
    List<Task> findByStatus(String status);
}