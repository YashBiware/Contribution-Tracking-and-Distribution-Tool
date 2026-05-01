package com.ctdt.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal; import java.time.LocalDateTime;
public class PerformanceDataPoint {
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss") private LocalDateTime timestamp;
    private Long executionTimeMs; private BigDecimal totalProjectWeight;
    public PerformanceDataPoint(){}
    public PerformanceDataPoint(LocalDateTime ts,Long ms,BigDecimal w){timestamp=ts;executionTimeMs=ms;totalProjectWeight=w;}
    public LocalDateTime getTimestamp(){return timestamp;} public void setTimestamp(LocalDateTime v){timestamp=v;}
    public Long getExecutionTimeMs(){return executionTimeMs;} public void setExecutionTimeMs(Long v){executionTimeMs=v;}
    public BigDecimal getTotalProjectWeight(){return totalProjectWeight;} public void setTotalProjectWeight(BigDecimal v){totalProjectWeight=v;}
}
