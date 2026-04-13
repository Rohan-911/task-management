package com.task.commentService.entity;

import java.util.Date;

import jakarta.persistence.*;

@Entity
@Table(name = "Comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "commentID")
    private Integer commentID;

    @Column(name = "content", nullable = false)
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createdAt")
    private Date createdAt;

   
    @Column(name = "taskID")
    private Integer taskID;

    @Column(name = "userID")
    private Integer userID;

    // Constructors
    public Comment() {}

    public Comment(Integer commentID, String content, Date createdAt, Integer taskID, Integer userID) {
        this.commentID = commentID;
        this.content = content;
        this.createdAt = createdAt;
        this.taskID = taskID;
        this.userID = userID;
    }

    // Getters & Setters
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