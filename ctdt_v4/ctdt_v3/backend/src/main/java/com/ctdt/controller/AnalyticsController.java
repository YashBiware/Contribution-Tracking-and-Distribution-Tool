package com.ctdt.controller;
import com.ctdt.dto.*; import com.ctdt.service.AnalyticsService;
import org.springframework.http.ResponseEntity; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/projects")
public class AnalyticsController {
    private final AnalyticsService svc;
    public AnalyticsController(AnalyticsService s){svc=s;}
    @GetMapping("/{pid}/analytics/distribution") public ResponseEntity<AnalyticsDistributionResponse> dist(@PathVariable Long pid){return ResponseEntity.ok(svc.getDistribution(pid));}
    @GetMapping("/{pid}/analytics/performance")  public ResponseEntity<AnalyticsPerformanceResponse>  perf(@PathVariable Long pid){return ResponseEntity.ok(svc.getPerformance(pid));}
    @GetMapping("/{pid}/analytics/summary")      public ResponseEntity<AnalyticsSummaryResponse>      sum (@PathVariable Long pid){return ResponseEntity.ok(svc.getSummary(pid));}
}
