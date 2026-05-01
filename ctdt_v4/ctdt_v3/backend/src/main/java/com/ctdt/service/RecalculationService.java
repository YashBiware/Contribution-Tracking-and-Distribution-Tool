package com.ctdt.service;
import com.ctdt.concurrency.ProjectLockManager;
import com.ctdt.dto.*;
import com.ctdt.entity.*;
import com.ctdt.exception.*;
import com.ctdt.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
@Service
public class RecalculationService {
    private final ContributionLogRepository lr;
    private final ContributionResultRepository rr;
    private final ProjectRepository pr;
    private final RecalculationHistoryRepository hr;
    private final ProjectLockManager lm;

    // Self-inject via @Lazy so Spring proxy wraps the @Transactional execute() call
    @Lazy @Autowired private RecalculationService self;

    public RecalculationService(ContributionLogRepository lr,ContributionResultRepository rr,
                                ProjectRepository pr,RecalculationHistoryRepository hr,
                                ProjectLockManager lm){
        this.lr=lr;this.rr=rr;this.pr=pr;this.hr=hr;this.lm=lm;
    }

    public RecalculateResponse recalculate(Long pid){
        long start=System.currentTimeMillis();
        boolean locked;
        try{locked=lm.tryLock(pid);}
        catch(InterruptedException ie){Thread.currentThread().interrupt();throw new RecalculationLockException(pid);}
        if(!locked)throw new RecalculationLockException(pid);
        try{return self.execute(pid,start);}
        finally{lm.unlock(pid);}
    }

    @Transactional(isolation=Isolation.SERIALIZABLE)
    public RecalculateResponse execute(Long pid,long start){
        Project p=pr.findByIdForUpdate(pid).orElseThrow(()->new ResourceNotFoundException("Project not found: "+pid));
        List<ContributionLog> logs=lr.findRetainedByProject(pid);
        if(logs.isEmpty()){
            hr.save(RecalculationHistory.of(p,BigDecimal.ZERO,System.currentTimeMillis()-start));
            return new RecalculateResponse(pid,"No retained contributions.",0,Collections.emptyList());
        }
        Map<Long,BigDecimal> totals=new LinkedHashMap<>();
        Map<Long,String> names=new LinkedHashMap<>();
        for(ContributionLog l:logs){
            Long cid=l.getContributor().getContributorId();
            totals.merge(cid,l.getContentSize().multiply(l.getWeightFactor()),BigDecimal::add);
            names.putIfAbsent(cid,l.getContributor().getContributorName());
        }
        BigDecimal total=totals.values().stream().reduce(BigDecimal.ZERO,BigDecimal::add);
        List<ResultResponse> results=new ArrayList<>();
        for(Map.Entry<Long,BigDecimal> e:totals.entrySet()){
            BigDecimal pct=total.compareTo(BigDecimal.ZERO)==0
                ?BigDecimal.ZERO.setScale(2,RoundingMode.HALF_UP)
                :e.getValue().divide(total,10,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2,RoundingMode.HALF_UP);
            ContributionResult res=rr.findByContributor_ContributorIdAndProject_ProjectId(e.getKey(),pid).orElseGet(()->{
                ContributionResult n=new ContributionResult();
                n.setContributor(logs.stream().filter(l->l.getContributor().getContributorId().equals(e.getKey())).findFirst().orElseThrow().getContributor());
                n.setProject(p);return n;
            });
            res.setPercentage(pct);rr.save(res);
            ResultResponse d=new ResultResponse();
            d.setContributorName(names.get(e.getKey()));d.setPercentage(pct);results.add(d);
        }
        results.sort(Comparator.comparing(ResultResponse::getPercentage).reversed());
        hr.save(RecalculationHistory.of(p,total,System.currentTimeMillis()-start));
        return new RecalculateResponse(pid,"Recalculation completed.",results.size(),results);
    }

    @Transactional(readOnly=true)
    public List<ResultResponse> getResults(Long pid){
        pr.findById(pid).orElseThrow(()->new ResourceNotFoundException("Project not found: "+pid));
        return rr.findByProject_ProjectIdOrderByPercentageDesc(pid).stream().map(r->{
            ResultResponse d=new ResultResponse();
            d.setContributorName(r.getContributor().getContributorName());
            d.setPercentage(r.getPercentage());return d;
        }).toList();
    }

    @Transactional(readOnly=true)
    public List<RecalculationHistoryResponse> getHistory(Long pid){
        pr.findById(pid).orElseThrow(()->new ResourceNotFoundException("Project not found: "+pid));
        return hr.findByProject_ProjectIdOrderByTriggeredAtDesc(pid).stream()
            .map(h->new RecalculationHistoryResponse(h.getTriggeredAt(),h.getTotalProjectWeight(),h.getExecutionTimeMs()))
            .toList();
    }

    @Transactional(readOnly=true)
    public List<RecalculationHistoryResponse> getHistoryPaged(Long pid,int limit){
        pr.findById(pid).orElseThrow(()->new ResourceNotFoundException("Project not found: "+pid));
        return hr.findRecentByProjectId(pid,Math.min(Math.max(limit,1),500)).stream()
            .map(h->new RecalculationHistoryResponse(h.getTriggeredAt(),h.getTotalProjectWeight(),h.getExecutionTimeMs()))
            .toList();
    }
}
