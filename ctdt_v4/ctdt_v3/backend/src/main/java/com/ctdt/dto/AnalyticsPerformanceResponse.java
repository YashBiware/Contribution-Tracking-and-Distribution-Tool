package com.ctdt.dto;
import java.util.List;
public class AnalyticsPerformanceResponse {
    private List<PerformanceDataPoint> history;
    public AnalyticsPerformanceResponse(){} public AnalyticsPerformanceResponse(List<PerformanceDataPoint> h){history=h;}
    public List<PerformanceDataPoint> getHistory(){return history;} public void setHistory(List<PerformanceDataPoint> v){history=v;}
}
