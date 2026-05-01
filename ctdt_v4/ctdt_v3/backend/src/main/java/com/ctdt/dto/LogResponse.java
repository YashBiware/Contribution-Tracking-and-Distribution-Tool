package com.ctdt.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal; import java.time.LocalDateTime;
public class LogResponse {
    private Long logId; private Long contributorId; private String contributorName;
    private Long projectId; private String fileName; private BigDecimal contentSize;
    private String contributionType; private BigDecimal weightFactor; private Boolean retainedFlag;
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss") private LocalDateTime timestamp;
    public Long getLogId(){return logId;} public void setLogId(Long v){this.logId=v;}
    public Long getContributorId(){return contributorId;} public void setContributorId(Long v){this.contributorId=v;}
    public String getContributorName(){return contributorName;} public void setContributorName(String v){this.contributorName=v;}
    public Long getProjectId(){return projectId;} public void setProjectId(Long v){this.projectId=v;}
    public String getFileName(){return fileName;} public void setFileName(String v){this.fileName=v;}
    public BigDecimal getContentSize(){return contentSize;} public void setContentSize(BigDecimal v){this.contentSize=v;}
    public String getContributionType(){return contributionType;} public void setContributionType(String v){this.contributionType=v;}
    public BigDecimal getWeightFactor(){return weightFactor;} public void setWeightFactor(BigDecimal v){this.weightFactor=v;}
    public Boolean getRetainedFlag(){return retainedFlag;} public void setRetainedFlag(Boolean v){this.retainedFlag=v;}
    public LocalDateTime getTimestamp(){return timestamp;} public void setTimestamp(LocalDateTime v){this.timestamp=v;}
}
