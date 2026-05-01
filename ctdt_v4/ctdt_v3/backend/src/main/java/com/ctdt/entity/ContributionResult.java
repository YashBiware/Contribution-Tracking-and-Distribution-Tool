package com.ctdt.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity @Table(name="contribution_result")
public class ContributionResult {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="result_id") private Long resultId;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="contributor_id",nullable=false) private Contributor contributor;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="project_id",nullable=false) private Project project;
    @Column(name="percentage",nullable=false,precision=5,scale=2) private BigDecimal percentage=BigDecimal.ZERO;
    @Column(name="last_updated") private LocalDateTime lastUpdated;
    @PrePersist @PreUpdate void touch(){lastUpdated=LocalDateTime.now();}
    public Long getResultId(){return resultId;}
    public Contributor getContributor(){return contributor;} public void setContributor(Contributor v){this.contributor=v;}
    public Project getProject(){return project;} public void setProject(Project v){this.project=v;}
    public BigDecimal getPercentage(){return percentage;} public void setPercentage(BigDecimal v){this.percentage=v;}
    public LocalDateTime getLastUpdated(){return lastUpdated;}
}
