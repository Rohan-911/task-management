package com.task.attachmentService.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.task.attachmentService.entitiy.Attachment;
import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Integer> {
   List<Attachment> findByTask_TaskID(Integer taskID);
}
