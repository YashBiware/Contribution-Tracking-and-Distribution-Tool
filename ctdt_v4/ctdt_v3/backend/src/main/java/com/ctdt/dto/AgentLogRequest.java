package com.ctdt.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public class AgentLogRequest {
    @NotBlank private String username;
    @NotBlank private String password;
    private Long projectId;        // numeric project id (legacy)
    private String joinToken;      // join token (preferred)
    @NotBlank private String fileName;
    @NotNull @Positive private BigDecimal contentSize;
    public String getUsername(){return username;} public void setUsername(String v){this.username=v;}
    public String getPassword(){return password;} public void setPassword(String v){this.password=v;}
    public Long getProjectId(){return projectId;} public void setProjectId(Long v){this.projectId=v;}
    public String getJoinToken(){return joinToken;} public void setJoinToken(String v){this.joinToken=v;}
    public String getFileName(){return fileName;} public void setFileName(String v){this.fileName=v;}
    public BigDecimal getContentSize(){return contentSize;} public void setContentSize(BigDecimal v){this.contentSize=v;}
}
