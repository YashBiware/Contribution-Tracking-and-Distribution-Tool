package com.ctdt.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
@Entity @Table(name="recalculation_history")
public class RecalculationHistory {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="history_id") private Long historyId;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="project_id",nullable=false) private Project project;
    @Column(name="triggered_at",updatable=false) private LocalDateTime triggeredAt;
    @Column(name="total_project_weight",updatable=false,precision=18,scale=4) private BigDecimal totalProjectWeight;
    @Column(name="execution_time_ms",updatable=false) private Long executionTimeMs;
    protected RecalculationHistory(){}
    @PrePersist void pre(){if(triggeredAt==null)triggeredAt=LocalDateTime.now();}
    public static RecalculationHistory of(Project p,BigDecimal w,long ms){
        RecalculationHistory h=new RecalculationHistory();
        h.project=p; h.totalProjectWeight=w.setScale(4,RoundingMode.HALF_UP); h.executionTimeMs=ms; return h;
    }
    public Long getHistoryId(){return historyId;}
    public Project getProject(){return project;}
    public LocalDateTime getTriggeredAt(){return triggeredAt;}
    public BigDecimal getTotalProjectWeight(){return totalProjectWeight;}
    public Long getExecutionTimeMs(){return executionTimeMs;}
}
