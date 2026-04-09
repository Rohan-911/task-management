package com.task.taskservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.task.taskservice.entity.Task;
import com.task.taskservice.service.TaskService;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService service;

    @PostMapping
    public Task createTask(@RequestBody Task task) {
        return service.createTask(task);
    }

    @GetMapping
    public List<Task> getAllTasks() {
        return service.getAllTasks();
    }

    @GetMapping("/{id}")
    public Task getTask(@PathVariable Integer id) {
        return service.getTaskById(id);
    }

    @GetMapping("/user/{userId}")
    public List<Task> getTasksByUser(@PathVariable Integer userId) {
        return service.getTasksByUser(userId);
    }

    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Integer id, @RequestBody Task task) {
        return service.updateTask(id, task);
    }

    @DeleteMapping("/{id}")
    public String deleteTask(@PathVariable Integer id) {
        service.deleteTask(id);
        return "Deleted Successfully";
    }
}