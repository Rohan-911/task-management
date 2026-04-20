package com.frontend.attachmentservice.dto;

import com.frontend.taskservice.dto.TaskDTO;

public class AttachmentDTO {

    private Integer attachmentID;
    private String fileName;
    private String filePath;
    private TaskDTO task;

    public AttachmentDTO() {
        this.task = new TaskDTO();
    }

    public Integer getAttachmentID() {
        return attachmentID;
    }

    public void setAttachmentID(Integer attachmentID) {
        this.attachmentID = attachmentID;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public TaskDTO getTask() {
        return task;
    }

    public void setTask(TaskDTO task) {
        this.task = task;
    }
}