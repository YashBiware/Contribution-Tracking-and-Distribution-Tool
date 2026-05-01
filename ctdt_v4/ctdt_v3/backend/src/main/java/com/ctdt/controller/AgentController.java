package com.ctdt.controller;
import com.ctdt.dto.*;
import com.ctdt.entity.*;
import com.ctdt.exception.*;
import com.ctdt.repository.*;
import com.ctdt.service.*;
import com.ctdt.util.WeightResolver;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController
@RequestMapping("/api/agent")
public class AgentController {
    private final ContributorRepository cr;
    private final ContributionLogRepository lr;
    private final ProjectRepository pr;
    private final RecalculationService rs;
    private final WeightResolver wr;
    private final ProjectService ps;

    public AgentController(ContributorRepository cr,ContributionLogRepository lr,
                           ProjectRepository pr,RecalculationService rs,WeightResolver wr,
                           ProjectService ps){
        this.cr=cr;this.lr=lr;this.pr=pr;this.rs=rs;this.wr=wr;this.ps=ps;
    }

    /** Verify credentials and return contributor + project info.
     *  Accepts either numeric projectId OR a joinToken string. */
    @PostMapping("/login")
    @Transactional(readOnly=true)
    public ResponseEntity<?> login(@RequestBody Map<String,Object> body){
        String username=((String)body.get("username")).toLowerCase().trim();
        String password=(String)body.get("password");
        // Support both joinToken string and numeric projectId
        Long projectId=resolveProjectId(body);
        Contributor c=cr.findByUsernameAndProject(username,projectId)
            .orElseThrow(()->new ResourceNotFoundException("Invalid username or project."));
        String hash=ContributorService.sha256(password);
        if(!hash.equals(c.getPasswordHash()))
            throw new BadRequestException("Invalid password.");
        return ResponseEntity.ok(Map.of(
            "contributorId",c.getContributorId(),
            "contributorName",c.getContributorName(),
            "projectId",projectId,
            "projectName",c.getProject().getProjectName()
        ));
    }

    /** Agent sends a file-change log silently — auto-triggers recalculation. */
    @PostMapping("/log")
    @Transactional
    public ResponseEntity<?> submitLog(@Valid @RequestBody AgentLogRequest req){
        Long projectId=resolveProjectIdFromReq(req);
        Contributor c=cr.findByUsernameAndProject(
            req.getUsername().toLowerCase().trim(),projectId)
            .orElseThrow(()->new ResourceNotFoundException("Invalid contributor."));
        String hash=ContributorService.sha256(req.getPassword());
        if(!hash.equals(c.getPasswordHash()))
            throw new BadRequestException("Invalid password.");
        Project p=c.getProject();
        ContributionLog log=new ContributionLog();
        log.setContributor(c); log.setProject(p);
        log.setFileName(req.getFileName());
        log.setContentSize(req.getContentSize());
        log.setWeightFactor(wr.resolve(req.getFileName()));
        log.setContributionType(ContributionLog.Type.ORIGINAL);
        log.setRetainedFlag(Boolean.TRUE);
        lr.save(log);
        rs.recalculate(p.getProjectId());
        return ResponseEntity.ok(Map.of("status","logged","file",req.getFileName()));
    }

    /** Resolve projectId from request body — accepts joinToken string or numeric projectId */
    private Long resolveProjectId(Map<String,Object> body){
        Object raw=body.get("projectId");
        Object token=body.get("joinToken");
        if(token!=null&&!token.toString().isBlank()){
            return ps.findByJoinToken(token.toString()).getProjectId();
        }
        if(raw!=null){
            try{return Long.valueOf(raw.toString());}catch(NumberFormatException e){
                // maybe it's actually a token in the projectId field
                return ps.findByJoinToken(raw.toString()).getProjectId();
            }
        }
        throw new BadRequestException("Must provide projectId or joinToken.");
    }

    private Long resolveProjectIdFromReq(AgentLogRequest req){
        if(req.getJoinToken()!=null&&!req.getJoinToken().isBlank()){
            return ps.findByJoinToken(req.getJoinToken()).getProjectId();
        }
        if(req.getProjectId()!=null) return req.getProjectId();
        throw new BadRequestException("Must provide projectId or joinToken.");
    }
}
