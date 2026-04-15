package com.task.taskservice.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.task.taskservice.entity.Task;
import com.task.taskservice.exception.ResourceNotFoundException;
import com.task.taskservice.repository.TaskRepository;

public class TaskServiceTest {

    @Mock
    private TaskRepository repo;

    @InjectMocks
    private TaskService service;

    public TaskServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

  
    @Test
    void testCreateTask() {
        Task task = new Task();
        task.setTaskName("Test Task");

        when(repo.save(task)).thenReturn(task);

        Task result = service.createTask(task);

        assertNotNull(result);
        assertEquals("Test Task", result.getTaskName());
    }

    
    @Test
    void testGetAllTasks() {
        List<Task> list = new ArrayList<>();
        list.add(new Task());
        list.add(new Task());

        when(repo.findAll()).thenReturn(list);

        List<Task> result = service.getAllTasks();

        assertEquals(2, result.size());
    }

   
    @Test
    void testGetTaskById() {
        Task task = new Task();
        task.setTaskID(1);

        when(repo.findById(1)).thenReturn(Optional.of(task));

        Task result = service.getTaskById(1);

        assertNotNull(result);
        assertEquals(1, result.getTaskID());
    }

    
    @Test
    void testGetTaskById_NotFound() {
        when(repo.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            service.getTaskById(1);
        });
    }

    
    @Test
    void testUpdateTask() {
        Task existing = new Task();
        existing.setTaskID(1);
        existing.setTaskName("Old");

        Task updated = new Task();
        updated.setTaskName("New");

        when(repo.findById(1)).thenReturn(Optional.of(existing));
        when(repo.save(any(Task.class))).thenReturn(existing);

        Task result = service.updateTask(1, updated);

        assertEquals("New", result.getTaskName());
    }

    
    @Test
    void testUpdateTask_NotFound() {
        Task updated = new Task();

        when(repo.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            service.updateTask(1, updated);
        });
    }

   
    @Test
    void testDeleteTask() {
        doNothing().when(repo).deleteById(1);

        service.deleteTask(1);

        verify(repo, times(1)).deleteById(1);
    }

   
    @Test
    void testGetTasksByStatus() {
        List<Task> list = new ArrayList<>();
        list.add(new Task());

        when(repo.findByStatus("Pending")).thenReturn(list);

        List<Task> result = service.getTasksByStatus("Pending");

        assertEquals(1, result.size());
    }

   
    @Test
    void testGetTasksByPriority() {
        List<Task> list = new ArrayList<>();
        list.add(new Task());

        when(repo.findByPriority("High")).thenReturn(list);

        List<Task> result = service.getTasksByPriority("High");

        assertEquals(1, result.size());
    }

    
    @Test
    void testGetTasksByUser() {
        List<Task> list = new ArrayList<>();
        list.add(new Task());

        when(repo.findByUserID(1)).thenReturn(list);

        List<Task> result = service.getTasksByUser(1);

        assertEquals(1, result.size());
    }

  
    @Test
    void testCountTasksByStatus() {
        when(repo.countByStatus("Pending")).thenReturn(5L);

        long result = service.countTasksByStatus("Pending");

        assertEquals(5, result);
    }
}