package com.task.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.task.entity.Task;
import com.task.exception.ResourceNotFoundException;
import com.task.repository.TaskRepository;

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
    	return repo.findById(id)
    		    .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
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

	public List<Task> getTasksByStatus(String status) {
		// TODO Auto-generated method stub
		return repo.findByStatus(status);
	}
}