package com.task.attachmentService;

import com.task.attachmentService.entitiy.Attachment;
import com.task.attachmentService.repository.AttachmentRepository;
import com.task.attachmentService.service.AttachmentService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AttachmentServiceTest {

    @Mock
    private AttachmentRepository repo;

    @InjectMocks
    private AttachmentService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // TEST SAVE
    @Test
    void testSaveAttachment() {
        Attachment a = new Attachment();
        a.setAttachmentID(200);
        a.setFileName("junit.txt");

        when(repo.save(a)).thenReturn(a);

        Attachment saved = service.save(a);

        assertNotNull(saved);
        assertEquals("junit.txt", saved.getFileName());
    }

    // TEST GET ALL
    @Test
    void testGetAll() {
        when(repo.findAll()).thenReturn(List.of(new Attachment()));

        List<Attachment> list = service.getAll();

        assertNotNull(list);
        assertEquals(1, list.size());
    }

    // TEST GET BY ID
    @Test
    void testGetById() {
        Attachment a = new Attachment();
        a.setAttachmentID(1);

        when(repo.findById(1)).thenReturn(Optional.of(a));

        Attachment result = service.getById(1);

        assertNotNull(result);
    }

    // TEST GET BY TASK
    @Test
    void testGetByTaskId() {
        when(repo.findByTask_TaskID(1)).thenReturn(List.of(new Attachment()));

        List<Attachment> list = service.getByTaskId(1);

        assertNotNull(list);
        assertEquals(1, list.size());
    }

    // TEST COUNT
    @Test
    void testCountByTask() {
        when(repo.countByTask_TaskID(1)).thenReturn(2);

        int count = service.countByTask(1);

        assertEquals(2, count);
    }

    // TEST DELETE
    @Test
    void testDelete() {
        doNothing().when(repo).deleteById(201);

        service.delete(201);

        verify(repo, times(1)).deleteById(201);
    }
}