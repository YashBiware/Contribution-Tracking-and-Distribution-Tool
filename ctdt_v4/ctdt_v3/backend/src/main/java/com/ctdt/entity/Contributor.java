package com.ctdt.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="contributor")
public class Contributor {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="contributor_id") private Long contributorId;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="project_id",nullable=false) private Project project;
    @Column(name="contributor_name",nullable=false) private String contributorName;
    @Column(name="username") private String username;
    @Column(name="password_hash") private String passwordHash;
    @Column(name="joined_at",updatable=false) private LocalDateTime joinedAt;
    @PrePersist void pre(){if(joinedAt==null)joinedAt=LocalDateTime.now();}
    public Long getContributorId(){return contributorId;}
    public Project getProject(){return project;} public void setProject(Project v){this.project=v;}
    public String getContributorName(){return contributorName;} public void setContributorName(String v){this.contributorName=v;}
    public String getUsername(){return username;} public void setUsername(String v){this.username=v;}
    public String getPasswordHash(){return passwordHash;} public void setPasswordHash(String v){this.passwordHash=v;}
    public LocalDateTime getJoinedAt(){return joinedAt;}
}
