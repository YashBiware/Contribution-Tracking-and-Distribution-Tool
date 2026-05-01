package com.ctdt.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
@Entity @Table(name="project")
public class Project {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="project_id") private Long projectId;
    @Column(name="project_name",nullable=false) private String projectName;
    @Column(name="directory_path") private String directoryPath;
    @Column(name="join_token",nullable=false,unique=true) private String joinToken;
    @Enumerated(EnumType.STRING) @Column(name="status",nullable=false) private Status status=Status.ACTIVE;
    @Column(name="created_at",updatable=false) private LocalDateTime createdAt;
    @PrePersist void pre(){
        if(createdAt==null)createdAt=LocalDateTime.now();
        if(joinToken==null||joinToken.isBlank())joinToken=UUID.randomUUID().toString();
    }
    public enum Status{ACTIVE,ARCHIVED}
    public Long getProjectId(){return projectId;}
    public String getProjectName(){return projectName;} public void setProjectName(String v){this.projectName=v;}
    public String getDirectoryPath(){return directoryPath;} public void setDirectoryPath(String v){this.directoryPath=v;}
    public String getJoinToken(){return joinToken;} public void setJoinToken(String v){this.joinToken=v;}
    public Status getStatus(){return status;} public void setStatus(Status v){this.status=v;}
    public LocalDateTime getCreatedAt(){return createdAt;}
}
