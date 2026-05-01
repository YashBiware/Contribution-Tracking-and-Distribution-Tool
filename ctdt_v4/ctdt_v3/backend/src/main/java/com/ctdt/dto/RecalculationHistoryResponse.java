package com.ctdt.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal; import java.time.LocalDateTime;
public class RecalculationHistoryResponse {
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss") private LocalDateTime triggeredAt;
    private BigDecimal totalProjectWeight; private Long executionTimeMs;
    public RecalculationHistoryResponse(){}
    public RecalculationHistoryResponse(LocalDateTime t,BigDecimal w,Long ms){triggeredAt=t;totalProjectWeight=w;executionTimeMs=ms;}
    public LocalDateTime getTriggeredAt(){return triggeredAt;} public void setTriggeredAt(LocalDateTime v){this.triggeredAt=v;}
    public BigDecimal getTotalProjectWeight(){return totalProjectWeight;} public void setTotalProjectWeight(BigDecimal v){this.totalProjectWeight=v;}
    public Long getExecutionTimeMs(){return executionTimeMs;} public void setExecutionTimeMs(Long v){this.executionTimeMs=v;}
}
