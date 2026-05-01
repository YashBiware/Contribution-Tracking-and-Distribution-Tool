package com.ctdt.dto;
import java.math.BigDecimal;
public class ResultResponse {
    private String contributorName; private BigDecimal percentage;
    public String getContributorName(){return contributorName;} public void setContributorName(String v){this.contributorName=v;}
    public BigDecimal getPercentage(){return percentage;} public void setPercentage(BigDecimal v){this.percentage=v;}
}
