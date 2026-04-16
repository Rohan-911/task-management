package com.task.commentService;

import com.task.commentService.controller.CommentController;
import com.task.commentService.entity.Comment;
import com.task.commentService.service.CommentService;

import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentControllerTest {

    @Mock
    private CommentService service;

    @InjectMocks
    private CommentController controller;

    public CommentControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    // CREATE
    @Test
    void testAddComment() {
        Comment c = new Comment();
        c.setCommentID(1);
        c.setContent("Test Comment");
        c.setTaskID(1);
        c.setUserID(1);

        when(service.save(c)).thenReturn(c);

        Comment result = controller.add(c);

        assertNotNull(result);
        assertEquals("Test Comment", result.getContent());
    }

    // GET ALL
    @Test
    void testGetAll() {
        List<Comment> list = new ArrayList<>();
        list.add(new Comment());

        when(service.getAll()).thenReturn(list);

        List<Comment> result = controller.getAll();

        assertEquals(1, result.size());
    }

    // GET BY ID
    @Test
    void testGetById() {
        Comment c = new Comment();
        c.setCommentID(1);

        when(service.getById(1)).thenReturn(c);

        Comment result = controller.getById(1);

        assertNotNull(result);
    }

    // GET BY TASK
    @Test
    void testGetByTask() {
        List<Comment> list = List.of(new Comment());

        when(service.getByTaskId(1)).thenReturn(list); // FIXED

        List<Comment> result = controller.getByTask(1); // FIXED

        assertEquals(1, result.size());

    }

    // GET BY USER
    @Test
    void testGetByUser() {
        List<Comment> list = List.of(new Comment());

        when(service.getByUser(1)).thenReturn(list);

        List<Comment> result = controller.getByUser(1);

        assertEquals(1, result.size());
    }

    // DELETE
    @Test
    void testDelete() {
        doNothing().when(service).delete(1);

        String result = controller.delete(1);

        assertEquals("Comment deleted", result); // FIXED
        verify(service, times(1)).delete(1);
    }
}