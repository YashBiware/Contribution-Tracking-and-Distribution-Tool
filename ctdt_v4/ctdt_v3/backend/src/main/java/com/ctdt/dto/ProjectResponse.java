package com.ctdt.dto;
import java.time.LocalDateTime;
public class ProjectResponse {
    private Long projectId;
    private String projectName;
    private String directoryPath;
    private String joinToken;
    private String status;
    private LocalDateTime createdAt;
    public Long getProjectId(){return projectId;} public void setProjectId(Long v){this.projectId=v;}
    public String getProjectName(){return projectName;} public void setProjectName(String v){this.projectName=v;}
    public String getDirectoryPath(){return directoryPath;} public void setDirectoryPath(String v){this.directoryPath=v;}
    public String getJoinToken(){return joinToken;} public void setJoinToken(String v){this.joinToken=v;}
    public String getStatus(){return status;} public void setStatus(String v){this.status=v;}
    public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime v){this.createdAt=v;}
}
