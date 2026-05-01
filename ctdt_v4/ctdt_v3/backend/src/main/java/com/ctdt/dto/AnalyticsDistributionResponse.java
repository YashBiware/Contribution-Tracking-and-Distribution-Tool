package com.ctdt.dto;
import java.math.BigDecimal; import java.util.List;
public class AnalyticsDistributionResponse {
    private Long projectId; private List<ContributorDistributionItem> contributors; private BigDecimal total;
    public AnalyticsDistributionResponse(){}
    public AnalyticsDistributionResponse(Long p,List<ContributorDistributionItem> c,BigDecimal t){projectId=p;contributors=c;total=t;}
    public Long getProjectId(){return projectId;} public void setProjectId(Long v){projectId=v;}
    public List<ContributorDistributionItem> getContributors(){return contributors;} public void setContributors(List<ContributorDistributionItem> v){contributors=v;}
    public BigDecimal getTotal(){return total;} public void setTotal(BigDecimal v){total=v;}
}
