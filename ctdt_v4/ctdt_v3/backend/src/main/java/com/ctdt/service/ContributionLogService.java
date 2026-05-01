package com.ctdt.service;
import com.ctdt.dto.*;
import com.ctdt.entity.*;
import com.ctdt.exception.*;
import com.ctdt.repository.*;
import com.ctdt.util.WeightResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Log submission is atomic:
 *  - The log row is persisted inside the same TX that RecalculationService.execute()
 *    opens under SERIALIZABLE isolation.
 *  - recalculate() acquires the project lock first, then calls self.execute()
 *    which reads ALL retained logs (including the one just saved).
 *  - No manual log entry is ever needed from the UI — the agent handles it silently.
 */
@Service
public class ContributionLogService {
    private final ContributionLogRepository lr;
    private final ContributorRepository cr;
    private final ProjectRepository pr;
    private final WeightResolver wr;
    private final RecalculationService rs;
    public ContributionLogService(ContributionLogRepository lr,ContributorRepository cr,
                                   ProjectRepository pr,WeightResolver wr,RecalculationService rs){
        this.lr=lr;this.cr=cr;this.pr=pr;this.wr=wr;this.rs=rs;
    }

    @Transactional
    public LogResponse submit(LogRequest req){
        Project p=pr.findById(req.getProjectId())
            .orElseThrow(()->new ResourceNotFoundException("Project not found: "+req.getProjectId()));
        Contributor c=cr.findById(req.getContributorId())
            .orElseThrow(()->new ResourceNotFoundException("Contributor not found: "+req.getContributorId()));
        if(!c.getProject().getProjectId().equals(p.getProjectId()))
            throw new BadRequestException("Contributor does not belong to this project.");
        ContributionLog l=new ContributionLog();
        l.setContributor(c); l.setProject(p);
        l.setFileName(req.getFileName());
        l.setContentSize(req.getContentSize());
        l.setWeightFactor(wr.resolve(req.getFileName()));
        l.setContributionType(ContributionLog.Type.valueOf(
            req.getContributionType()!=null?req.getContributionType().toUpperCase():"ORIGINAL"));
        l.setRetainedFlag(req.getRetainedFlag()!=null?req.getRetainedFlag():Boolean.TRUE);
        ContributionLog saved=lr.save(l);
        // Trigger atomic recalculation
        rs.recalculate(p.getProjectId());
        return toDto(saved);
    }

    private LogResponse toDto(ContributionLog l){
        LogResponse r=new LogResponse();
        r.setLogId(l.getLogId());
        r.setContributorId(l.getContributor().getContributorId());
        r.setContributorName(l.getContributor().getContributorName());
        r.setProjectId(l.getProject().getProjectId());
        r.setFileName(l.getFileName());
        r.setContentSize(l.getContentSize());
        r.setContributionType(l.getContributionType().name());
        r.setWeightFactor(l.getWeightFactor());
        r.setRetainedFlag(l.getRetainedFlag());
        r.setTimestamp(l.getTimestamp());
        return r;
    }
}
