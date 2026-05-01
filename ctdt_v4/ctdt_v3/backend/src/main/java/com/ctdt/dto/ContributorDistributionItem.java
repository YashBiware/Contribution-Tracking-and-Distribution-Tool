package com.ctdt.dto;
import java.math.BigDecimal;
public class ContributorDistributionItem {
    private String name; private BigDecimal percentage;
    public ContributorDistributionItem(){}
    public ContributorDistributionItem(String n,BigDecimal p){name=n;percentage=p;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public BigDecimal getPercentage(){return percentage;} public void setPercentage(BigDecimal v){percentage=v;}
}
