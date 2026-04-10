package com.task.attachmentService.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.task.attachmentService.entitiy.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, Integer> {
}