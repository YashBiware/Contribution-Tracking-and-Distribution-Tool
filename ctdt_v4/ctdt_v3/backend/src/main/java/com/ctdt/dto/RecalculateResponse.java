package com.ctdt.dto;
import java.util.List;
public class RecalculateResponse {
    private Long projectId; private String message; private int contributorsProcessed; private List<ResultResponse> results;
    public RecalculateResponse(){}
    public RecalculateResponse(Long pid,String msg,int c,List<ResultResponse> r){projectId=pid;message=msg;contributorsProcessed=c;results=r;}
    public Long getProjectId(){return projectId;} public void setProjectId(Long v){this.projectId=v;}
    public String getMessage(){return message;} public void setMessage(String v){this.message=v;}
    public int getContributorsProcessed(){return contributorsProcessed;} public void setContributorsProcessed(int v){this.contributorsProcessed=v;}
    public List<ResultResponse> getResults(){return results;} public void setResults(List<ResultResponse> v){this.results=v;}
}
