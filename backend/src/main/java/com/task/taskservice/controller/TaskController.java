package com.task.taskservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.task.taskservice.entity.Task;
import com.task.taskservice.service.TaskService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService service;

    @PostMapping("/create")
    public Task createTask(@Valid@RequestBody Task task) {
        return service.createTask(task);
    }

    @GetMapping("/all")
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

    @PutMapping("/update/{id}")
    public Task updateTask(@PathVariable Integer id, @RequestBody Task task) {
        return service.updateTask(id, task);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteTask(@PathVariable Integer id) {
        service.deleteTask(id);
        return "Deleted Successfully";
    }
    
    @GetMapping("/priority/{priority}")
    public List<Task> getTasksByPriority(@PathVariable String priority) {
        return service.getTasksByPriority(priority);
    }

    
    @GetMapping("/status/{status}")
    public List<Task> getTasksByStatus(@PathVariable String status) {
        return service.getTasksByStatus(status);
    }

   
    @GetMapping("/project/{projectId}")
    public List<Task> getTasksByProject(@PathVariable Integer projectId) {
        return service.getTasksByProject(projectId);
    }

     @GetMapping("/search/{name}")
    public List<Task> searchTasks(@PathVariable String name) {
        return service.searchTasks(name);
    }

    
    @GetMapping("/filter")
    public List<Task> filterTasks(
            @RequestParam String status,
            @RequestParam String priority) {
        return service.getTasksByStatusAndPriority(status, priority);
    }

    
    @GetMapping("/count/{status}")
    public long countTasksByStatus(@PathVariable String status) {
        return service.countTasksByStatus(status);
    }
}