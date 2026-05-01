package com.ctdt.service;
import com.ctdt.dto.*; import com.ctdt.entity.*; import com.ctdt.exception.ResourceNotFoundException;
import com.ctdt.repository.*;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal; import java.math.RoundingMode; import java.util.Comparator; import java.util.List;
@Service public class AnalyticsService {
    private final ProjectRepository pr; private final ContributionResultRepository rr;
    private final RecalculationHistoryRepository hr; private final ContributorRepository cr; private final ContributionLogRepository lr;
    public AnalyticsService(ProjectRepository pr,ContributionResultRepository rr,RecalculationHistoryRepository hr,ContributorRepository cr,ContributionLogRepository lr){this.pr=pr;this.rr=rr;this.hr=hr;this.cr=cr;this.lr=lr;}
    @Transactional(readOnly=true) public AnalyticsDistributionResponse getDistribution(Long pid){
        ok(pid); List<ContributionResult> rows=rr.findByProject_ProjectIdOrderByPercentageDesc(pid);
        List<ContributorDistributionItem> items=rows.stream().map(r->new ContributorDistributionItem(r.getContributor().getContributorName(),r.getPercentage().setScale(2,RoundingMode.HALF_UP))).toList();
        BigDecimal total=items.stream().map(ContributorDistributionItem::getPercentage).reduce(BigDecimal.ZERO,BigDecimal::add).setScale(2,RoundingMode.HALF_UP);
        return new AnalyticsDistributionResponse(pid,items,total);
    }
    @Transactional(readOnly=true) public AnalyticsPerformanceResponse getPerformance(Long pid){
        ok(pid); List<PerformanceDataPoint> pts=hr.findByProject_ProjectIdOrderByTriggeredAtDesc(pid).stream().sorted(Comparator.comparing(RecalculationHistory::getTriggeredAt)).map(h->new PerformanceDataPoint(h.getTriggeredAt(),h.getExecutionTimeMs(),h.getTotalProjectWeight())).toList();
        return new AnalyticsPerformanceResponse(pts);
    }
    @Transactional(readOnly=true) public AnalyticsSummaryResponse getSummary(Long pid){
        ok(pid); long tc=cr.findByProject_ProjectId(pid).size(); long tl=lr.countProjectLogs(pid);
        List<RecalculationHistory> latest=hr.findRecentByProjectId(pid,1);
        BigDecimal lw=BigDecimal.ZERO; Long le=null;
        if(!latest.isEmpty()){lw=latest.get(0).getTotalProjectWeight();le=latest.get(0).getExecutionTimeMs();}
        return new AnalyticsSummaryResponse(tc,tl,lw,le);
    }
    private void ok(Long pid){pr.findById(pid).orElseThrow(()->new ResourceNotFoundException("Project not found: "+pid));}
}
