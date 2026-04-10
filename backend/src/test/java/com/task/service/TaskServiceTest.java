package com.task.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.task.entity.Task;
import com.task.repository.TaskRepository;
import com.task.service.TaskService;

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
        task.setTaskID(1);
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

        when(repo.findAll()).thenReturn(list);

        List<Task> result = service.getAllTasks();

        assertEquals(1, result.size());
    }

    @Test
    void testDeleteTask() {
        doNothing().when(repo).deleteById(1);

        service.deleteTask(1);

        verify(repo, times(1)).deleteById(1);
    }
}