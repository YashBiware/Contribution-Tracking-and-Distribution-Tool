package com.ctdt.dto;
import java.math.BigDecimal;
public class AnalyticsSummaryResponse {
    private Long totalContributors; private Long totalLogs; private BigDecimal latestTotalWeight; private Long lastExecutionTimeMs;
    public AnalyticsSummaryResponse(){}
    public AnalyticsSummaryResponse(Long tc,Long tl,BigDecimal lw,Long le){totalContributors=tc;totalLogs=tl;latestTotalWeight=lw;lastExecutionTimeMs=le;}
    public Long getTotalContributors(){return totalContributors;} public void setTotalContributors(Long v){totalContributors=v;}
    public Long getTotalLogs(){return totalLogs;} public void setTotalLogs(Long v){totalLogs=v;}
    public BigDecimal getLatestTotalWeight(){return latestTotalWeight;} public void setLatestTotalWeight(BigDecimal v){latestTotalWeight=v;}
    public Long getLastExecutionTimeMs(){return lastExecutionTimeMs;} public void setLastExecutionTimeMs(Long v){lastExecutionTimeMs=v;}
}
