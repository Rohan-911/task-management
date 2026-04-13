package com.task.taskservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.task.taskservice.entity.Task;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {

    List<Task> findByUserID(Integer userID);

    List<Task> findByProjectID(Integer projectID);
}