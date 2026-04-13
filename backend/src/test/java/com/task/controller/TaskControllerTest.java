package com.task.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.task.controller.TaskController;
import com.task.entity.Task;
import com.task.service.TaskService;

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
}