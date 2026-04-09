package com.task.taskservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.task.taskservice.entity.Task;
import com.task.taskservice.repository.TaskRepository;

@Service
public class TaskService {

    @Autowired
    private TaskRepository repo;

    public Task createTask(Task task) {
        return repo.save(task);
    }

    public List<Task> getAllTasks() {
        return repo.findAll();
    }

    public Task getTaskById(Integer id) {
        return repo.findById(id).orElse(null);
    }

    public List<Task> getTasksByUser(Integer userId) {
        return repo.findByUserID(userId);
    }

    public Task updateTask(Integer id, Task task) {
        task.setTaskID(id);
        return repo.save(task);
    }

    public void deleteTask(Integer id) {
        repo.deleteById(id);
    }
}