package com.task.taskservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.task.taskservice.entity.Task;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {

    
    List<Task> findByPriority(String priority);

    List<Task> findByStatus(String status);

    List<Task> findByUserID(Integer userId);

    List<Task> findByProjectID(Integer projectId);


    List<Task> findByTaskNameContaining(String name);

    List<Task> findByStatusAndPriority(String status, String priority);

    long countByStatus(String status);
}