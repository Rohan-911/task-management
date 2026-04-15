package com.task.taskservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import com.task.exception.ResourceNotFoundException;
import com.task.taskservice.entity.Task;
import com.task.taskservice.repository.TaskRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository repo;

    @InjectMocks
    private TaskService service;

    private Task task;

    @BeforeEach
    void setup() {
        task = new Task();
        task.setTaskName("Test Task");
        task.setDescription("Test Desc");
        task.setStatus("OPEN");
        task.setPriority("HIGH");
        task.setUserID(101);
        task.setProjectID(201);
    }

    
    @Test
    void testCreateTask() {
        when(repo.save(task)).thenReturn(task);

        Task result = service.createTask(task);

        assertNotNull(result);
        assertEquals("Test Task", result.getTaskName());
        verify(repo, times(1)).save(task);
    }

    
    @Test
    void testGetAllTasks() {
        when(repo.findAll()).thenReturn(List.of(task));

        List<Task> tasks = service.getAllTasks();

        assertEquals(1, tasks.size());
        verify(repo).findAll();
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
        when(repo.findById(1)).thenReturn(Optional.of(task));
        when(repo.save(any(Task.class))).thenReturn(task);

        Task updated = new Task();
        updated.setTaskName("Updated Task");
        updated.setDescription("Updated Desc");
        updated.setStatus("DONE");
        updated.setPriority("LOW");

        Task result = service.updateTask(1, updated);

        assertEquals("Updated Task", result.getTaskName());
        verify(repo).save(any(Task.class));
    }

    
    @Test
    void testDeleteTask() {
        doNothing().when(repo).deleteById(1);

        service.deleteTask(1);

        verify(repo, times(1)).deleteById(1);
    }

    
    @Test
    void testGetTasksByPriority() {
        when(repo.findByPriority("HIGH")).thenReturn(List.of(task));

        List<Task> result = service.getTasksByPriority("HIGH");

        assertEquals(1, result.size());
    }

   
    @Test
    void testGetTasksByStatus() {
        when(repo.findByStatus("OPEN")).thenReturn(List.of(task));

        List<Task> result = service.getTasksByStatus("OPEN");

        assertEquals(1, result.size());
    }

    
    @Test
    void testSearchTasks() {
        when(repo.findByTaskNameContaining("Test")).thenReturn(List.of(task));

        List<Task> result = service.searchTasks("Test");

        assertFalse(result.isEmpty());
    }

    
    @Test
    void testCountTasksByStatus() {
        when(repo.countByStatus("OPEN")).thenReturn(5L);

        long count = service.countTasksByStatus("OPEN");

        assertEquals(5, count);
    }
}