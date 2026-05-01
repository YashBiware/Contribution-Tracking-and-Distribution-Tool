package com.ctdt.dto;
import jakarta.validation.constraints.*;
public class ProjectRequest {
    @NotBlank @Size(max=255) private String projectName;
    private String directoryPath;
    public String getProjectName(){return projectName;} public void setProjectName(String v){this.projectName=v;}
    public String getDirectoryPath(){return directoryPath;} public void setDirectoryPath(String v){this.directoryPath=v;}
}
