package com.task.attachmentService;

import com.task.attachmentService.entitiy.Attachment;
import com.task.attachmentService.service.AttachmentService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AttachmentServiceTest {

    @Autowired
    private AttachmentService service;

    // TEST SAVE
    @Test
    void testSaveAttachment() {
        Attachment a = new Attachment();
        a.setAttachmentID(200); // use new ID
        a.setFileName("junit.txt");
        a.setFilePath("C:/junit.txt");

        Attachment saved = service.save(a);

        assertNotNull(saved);
        assertEquals("junit.txt", saved.getFileName());
    }

    // TEST GET ALL
    @Test
    void testGetAll() {
        List<Attachment> list = service.getAll();
        assertNotNull(list);
    }

    // TEST GET BY ID
    @Test
    void testGetById() {
        Attachment a = service.getById(1);
        assertNotNull(a);
    }

    // TEST GET BY TASK
    @Test
    void testGetByTaskId() {
        List<Attachment> list = service.getByTaskId(1);
        assertNotNull(list);
    }

    // TEST COUNT
    @Test
    void testCountByTask() {
        int count = service.countByTask(1);
        assertTrue(count >= 0);
    }

    // TEST DELETE
    @Test
    void testDelete() {
        Attachment a = new Attachment();
        a.setAttachmentID(201);
        a.setFileName("delete.txt");
        a.setFilePath("C:/delete.txt");

        service.save(a);
        service.delete(201);

        Attachment deleted = service.getById(201);
        assertNull(deleted);
    }
}