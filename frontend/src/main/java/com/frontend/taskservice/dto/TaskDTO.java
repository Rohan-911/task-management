package com.frontend.taskservice.dto;

import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;

public class TaskDTO {
	private Integer taskID;

    
    public Integer getTaskID() {
		return taskID;
	}
	public void setTaskID(Integer taskID) {
		this.taskID = taskID;
	}
	private String taskName;
    private String description;
    private String status;
    private String priority;
    private Integer userID;
    private Integer projectID;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dueDate;

   
    public String getTaskName() {
        return taskName;
    }
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getPriority() {
        return priority;
    }
    public void setPriority(String priority) {
        this.priority = priority;
    }
    public Integer getUserID() {
        return userID;
    }
    public void setUserID(Integer userID) {
        this.userID = userID;
    }
    public Integer getProjectID() {
        return projectID;
    }
    public void setProjectID(Integer projectID) {
        this.projectID = projectID;
    }
    public Date getDueDate() {
        return dueDate;
    }
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
}