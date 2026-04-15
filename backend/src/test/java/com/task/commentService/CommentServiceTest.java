package com.task.commentService;

import com.task.commentService.entity.Comment;
import com.task.commentService.repository.CommentRepository;
import com.task.commentService.service.CommentService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    @Mock
    private CommentRepository repo;

    @InjectMocks
    private CommentService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // TEST SAVE
    @Test
    void testSaveComment() {
        Comment c = new Comment();
        c.setContent("JUnit Comment");
        c.setTaskID(1);
        c.setUserID(1);

        when(repo.save(any(Comment.class))).thenReturn(c);

        Comment saved = service.save(c);

        assertNotNull(saved);
        assertEquals("JUnit Comment", saved.getContent());
    }

    // TEST GET ALL
    @Test
    void testGetAll() {
        when(repo.findAll()).thenReturn(Arrays.asList(new Comment(), new Comment()));

        List<Comment> list = service.getAll();

        assertEquals(2, list.size());
    }

    // TEST GET BY ID
    @Test
    void testGetById() {
        Comment c = new Comment();
        c.setCommentID(1);

        when(repo.findById(1)).thenReturn(Optional.of(c));

        Comment result = service.getById(1);

        assertNotNull(result);
    }

    // TEST GET BY TASK
    @Test
    void testGetByTask() {
        when(repo.findByTask_TaskID(1)).thenReturn(Arrays.asList(new Comment()));

        List<Comment> list = service.getByTaskId(1);

        assertEquals(1, list.size());
    }

    // TEST GET BY USER
    @Test
    void testGetByUser() {
        when(repo.findByUserID(1)).thenReturn(Arrays.asList(new Comment()));

        List<Comment> list = service.getByUser(1);

        assertEquals(1, list.size());
    }

    // TEST DELETE
    @Test
    void testDelete() {
        doNothing().when(repo).deleteById(1);

        service.delete(1);

        verify(repo, times(1)).deleteById(1);
    }
    
    @Test
    void testGetById_NotFound() {
        when(repo.findById(99)).thenReturn(Optional.empty());

        Comment result = service.getById(99);

        assertNull(result);
    }
    
    @Test
    void testGetByTask_Empty() {
        when(repo.findByTask_TaskID(1)).thenReturn(Collections.emptyList());

        List<Comment> list = service.getByTaskId(1);

        assertTrue(list.isEmpty());
    }
    
    @Test
    void testSave_VerifyRepoCall() {
        Comment c = new Comment();

        when(repo.save(any())).thenReturn(c);

        service.save(c);

        verify(repo, times(1)).save(c);
    }
}