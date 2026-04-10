package com.task.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.task.entity.Task;
import com.task.service.TaskService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService service;

    @PostMapping("/create")
    public Task createTask(@Valid @RequestBody Task task) {
        return service.createTask(task);
    }

    @GetMapping("/all")
    public List<Task> getAllTasks() {
        return service.getAllTasks();
    }

    @GetMapping("/get/{id}")
    public Task getTask(@PathVariable Integer id) {
        return service.getTaskById(id);
    }

    @GetMapping("/user/{userId}")
    public List<Task> getTasksByUser(@PathVariable Integer userId) {
        return service.getTasksByUser(userId);
    }
    @GetMapping("/status/{status}")
    public List<Task> getByStatus(@PathVariable String status) {
        return service.getTasksByStatus(status);
    }

    @PutMapping("/update/{id}")
    public Task updateTask(@PathVariable Integer id, @RequestBody Task task) {
        return service.updateTask(id, task);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteTask(@PathVariable Integer id) {
        service.deleteTask(id);
        return "Deleted Successfully";
    }
}