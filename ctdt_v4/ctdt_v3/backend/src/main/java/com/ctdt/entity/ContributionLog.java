package com.ctdt.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity @Table(name="contribution_log")
public class ContributionLog {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="log_id") private Long logId;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="contributor_id",nullable=false) private Contributor contributor;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="project_id",nullable=false) private Project project;
    @Column(name="file_name",nullable=false) private String fileName;
    @Column(name="content_size",nullable=false,precision=18,scale=4) private BigDecimal contentSize;
    @Column(name="weight_factor",nullable=false,precision=5,scale=2) private BigDecimal weightFactor;
    @Enumerated(EnumType.STRING) @Column(name="contribution_type",nullable=false) private Type contributionType=Type.ORIGINAL;
    @Column(name="retained_flag",nullable=false) private Boolean retainedFlag=Boolean.TRUE;
    @Column(name="timestamp",updatable=false) private LocalDateTime timestamp;
    @PrePersist void pre(){if(timestamp==null)timestamp=LocalDateTime.now();}
    public enum Type{ORIGINAL,MODIFIED}
    public Long getLogId(){return logId;}
    public Contributor getContributor(){return contributor;} public void setContributor(Contributor v){this.contributor=v;}
    public Project getProject(){return project;} public void setProject(Project v){this.project=v;}
    public String getFileName(){return fileName;} public void setFileName(String v){this.fileName=v;}
    public BigDecimal getContentSize(){return contentSize;} public void setContentSize(BigDecimal v){this.contentSize=v;}
    public BigDecimal getWeightFactor(){return weightFactor;} public void setWeightFactor(BigDecimal v){this.weightFactor=v;}
    public Type getContributionType(){return contributionType;} public void setContributionType(Type v){this.contributionType=v;}
    public Boolean getRetainedFlag(){return retainedFlag;} public void setRetainedFlag(Boolean v){this.retainedFlag=v;}
    public LocalDateTime getTimestamp(){return timestamp;}
}
