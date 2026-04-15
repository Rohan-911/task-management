package com.task.commentService.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.task.commentService.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByTask_TaskID(Integer taskId);
    
    List<Comment> findByUserID(Integer userID);
}