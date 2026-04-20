package com.task.commentService.entity;

import java.util.Date;

import jakarta.persistence.*;
@Entity
@Table(name = "Comment")
public class Comment {

    @Id
   // @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CommentID")
    private Integer commentID;

    @Column(name = "Text", nullable = false)
    private String content;   // KEEP THIS NAME

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CreatedAt")
    private Date createdAt;

    @Column(name = "TaskID")
    private Integer taskID;

    @Column(name = "UserID")
    private Integer userID;

    // GETTERS & SETTERS

    public Integer getCommentID() {
        return commentID;
    }

    public void setCommentID(Integer commentID) {
        this.commentID = commentID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getTaskID() {
        return taskID;
    }

    public void setTaskID(Integer taskID) {
        this.taskID = taskID;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }
}