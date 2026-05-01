package com.ctdt.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
public class ContributorResponse {
    private Long contributorId; private String contributorName;
    private String username; private Long projectId;
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss") private LocalDateTime joinedAt;
    public Long getContributorId(){return contributorId;} public void setContributorId(Long v){this.contributorId=v;}
    public String getContributorName(){return contributorName;} public void setContributorName(String v){this.contributorName=v;}
    public String getUsername(){return username;} public void setUsername(String v){this.username=v;}
    public Long getProjectId(){return projectId;} public void setProjectId(Long v){this.projectId=v;}
    public LocalDateTime getJoinedAt(){return joinedAt;} public void setJoinedAt(LocalDateTime v){this.joinedAt=v;}
}
