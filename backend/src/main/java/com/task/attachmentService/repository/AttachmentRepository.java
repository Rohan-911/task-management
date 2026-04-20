package com.task.attachmentService.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

import com.task.attachmentService.entitiy.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, Integer> {

    // CORRECT (based on your Task entity)
    List<Attachment> findByTask_TaskID(Integer taskID);

    int countByTask_TaskID(Integer taskID);

    void deleteByTask_TaskID(Integer taskID);

    @Query("SELECT MAX(a.attachmentID) FROM Attachment a")
    Integer findMaxId();
}