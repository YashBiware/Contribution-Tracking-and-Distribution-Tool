package com.ctdt.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public class LogRequest {
    @NotNull private Long contributorId;
    @NotNull private Long projectId;
    @NotBlank @Size(max=512) private String fileName;
    @NotNull @DecimalMin("0.0001") private BigDecimal contentSize;
    private String contributionType="ORIGINAL";
    private Boolean retainedFlag=Boolean.TRUE;
    public Long getContributorId(){return contributorId;} public void setContributorId(Long v){this.contributorId=v;}
    public Long getProjectId(){return projectId;} public void setProjectId(Long v){this.projectId=v;}
    public String getFileName(){return fileName;} public void setFileName(String v){this.fileName=v;}
    public BigDecimal getContentSize(){return contentSize;} public void setContentSize(BigDecimal v){this.contentSize=v;}
    public String getContributionType(){return contributionType;} public void setContributionType(String v){this.contributionType=v;}
    public Boolean getRetainedFlag(){return retainedFlag;} public void setRetainedFlag(Boolean v){this.retainedFlag=v;}
}
