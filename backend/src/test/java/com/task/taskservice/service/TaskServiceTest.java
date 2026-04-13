package com.task.taskservice.service;

import com.task.taskservice.entity.Task;
import com.task.taskservice.repository.TaskRepository;
import com.task.taskservice.service.TaskService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TaskServiceTest {

    @Mock
    private TaskRepository repository;

    @InjectMocks
    private TaskService service;

    private Task task;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        task = new Task();
        task.setTaskID(1);
        task.setTaskName("Test Task");
        task.setStatus("Pending");
    }

    
    @Test
    void testCreateTask() {
        when(repository.save(task)).thenReturn(task);

        Task saved = service.createTask(task);

        assertNotNull(saved);
        assertEquals("Test Task", saved.getTaskName());
    }

   
    @Test
    void testGetAllTasks() {
        when(repository.findAll()).thenReturn(Arrays.asList(task));

        List<Task> tasks = service.getAllTasks();

        assertEquals(1, tasks.size());
    }

    
    @Test
    void testGetTaskById() {
        when(repository.findById(1)).thenReturn(Optional.of(task));

        Task found = service.getTaskById(1);

        assertEquals(1, found.getTaskID());
    }

    
    @Test
    void testDeleteTask() {
        doNothing().when(repository).deleteById(1);

        service.deleteTask(1);

        verify(repository, times(1)).deleteById(1);
    }
}