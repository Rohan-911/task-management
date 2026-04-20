package com.task.commentService.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.task.commentService.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByTaskID(Integer taskId);
    List<Comment> findByUserID(Integer userID);
    @Query("SELECT MAX(c.commentID) FROM Comment c")
    Integer findMaxId();
}