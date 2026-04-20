package com.task.attachmentController;

import com.task.attachmentService.controller.AttachmentController;
import com.task.attachmentService.entitiy.Attachment;
import com.task.attachmentService.service.AttachmentService;

import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AttachmentControllerTest {

    @Mock
    private AttachmentService service;

    @InjectMocks
    private AttachmentController controller;

    public AttachmentControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    // CREATE
    @Test
    void testAddAttachment() {
        Attachment a = new Attachment();
        a.setAttachmentID(1);
        a.setFileName("file.txt");

        when(service.save(a)).thenReturn(a);

        Attachment result = controller.add(a);

        assertNotNull(result);
        assertEquals("file.txt", result.getFileName());
    }

    //GET ALL
    @Test
    void testGetAll() {
        List<Attachment> list = List.of(new Attachment());

        when(service.getAll()).thenReturn(list);

        List<Attachment> result = controller.getAll();

        assertEquals(1, result.size());
    }
    //GET BY ID
    @Test
    void testGetById() {
        Attachment a = new Attachment();
        a.setAttachmentID(1);

        when(service.getById(1)).thenReturn(a);

        org.springframework.http.ResponseEntity<Attachment> result = controller.getById(1);

        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().getAttachmentID());
    }

    // GET BY TASK
    @Test
    void testGetByTask() {
        List<Attachment> list = List.of(new Attachment());

        when(service.getByTaskId(1)).thenReturn(list);

        List<Attachment> result = controller.getByTask(1);

        assertEquals(1, result.size());
    }

    // COUNT
    @Test
    void testCountByTask() {
        when(service.countByTask(1)).thenReturn(2);

        int result = controller.countByTask(1);

        assertEquals(2, result);
    }

    // DELETE
    @Test
    void testDelete() {
        doNothing().when(service).delete(1);

        String result = controller.delete(1);

        assertEquals("Attachment deleted successfully", result);
        verify(service, times(1)).delete(1);
    }

    // DELETE BY TASK
    @Test
    void testDeleteByTask() {
        doNothing().when(service).deleteByTask(1);

        String result = controller.deleteByTask(1);

        assertTrue(result.contains("All attachments deleted"));
        verify(service, times(1)).deleteByTask(1);
    }
}