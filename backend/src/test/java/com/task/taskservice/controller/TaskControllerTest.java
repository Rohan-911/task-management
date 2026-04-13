package com.task.taskservice.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.task.taskservice.controller.TaskController;
import com.task.taskservice.entity.Task;
import com.task.taskservice.service.TaskService;

public class TaskControllerTest {

    @Mock
    private TaskService service;

    @InjectMocks
    private TaskController controller;

    public TaskControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllTasks() {
        List<Task> list = new ArrayList<>();
        list.add(new Task());

        when(service.getAllTasks()).thenReturn(list);

        List<Task> result = controller.getAllTasks();

        assertEquals(1, result.size());
    }
    @Test
    void testCreateTask() {
        Task task = new Task();
        task.setTaskID(1);

        when(service.createTask(task)).thenReturn(task);

        Task result = controller.createTask(task);

        assertNotNull(result);
        assertEquals(1, result.getTaskID());
    }
    @Test
    void testGetTaskById() {
        Task task = new Task();
        task.setTaskID(1);

        when(service.getTaskById(1)).thenReturn(task);

        Task result = controller.getTask(1);

        assertNotNull(result);
        assertEquals(1, result.getTaskID());
    }
    @Test
    void testDeleteTask() {
        doNothing().when(service).deleteTask(1);

        String response = controller.deleteTask(1);

        assertEquals("Deleted Successfully", response);
        verify(service, times(1)).deleteTask(1);
    }
    @Test
    void testUpdateTask() {
        Task task = new Task();
        task.setTaskID(1);
        task.setTaskName("Updated Task");

        when(service.updateTask(1, task)).thenReturn(task);

        Task result = controller.updateTask(1, task);

        assertEquals("Updated Task", result.getTaskName());
    }
    @Test
    void testGetTasksByUser() {
        List<Task> list = new ArrayList<>();
        list.add(new Task());

        when(service.getTasksByUser(10)).thenReturn(list);

        List<Task> result = controller.getTasksByUser(10);

        assertEquals(1, result.size());
    }
}