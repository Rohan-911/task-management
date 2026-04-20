package com.frontend.commentservice.dto;

import java.util.Date;

public class CommentDTO {

    private Integer commentID;
    private String content;
    private Date createdAt;
    public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	private Integer taskID;
    private Integer userID;

    // getters setters
    public Integer getCommentID() { return commentID; }
    public void setCommentID(Integer commentID) { this.commentID = commentID; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getTaskID() { return taskID; }
    public void setTaskID(Integer taskID) { this.taskID = taskID; }

    public Integer getUserID() { return userID; }
    public void setUserID(Integer userID) { this.userID = userID; }
}