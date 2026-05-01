package com.ctdt.dto;
public class LoginRequest {
    private String type; // "admin" or "contributor"
    private String username;
    private String password;
    private Long projectId;
    public String getType(){return type;} public void setType(String v){this.type=v;}
    public String getUsername(){return username;} public void setUsername(String v){this.username=v;}
    public String getPassword(){return password;} public void setPassword(String v){this.password=v;}
    public Long getProjectId(){return projectId;} public void setProjectId(Long v){this.projectId=v;}
}
