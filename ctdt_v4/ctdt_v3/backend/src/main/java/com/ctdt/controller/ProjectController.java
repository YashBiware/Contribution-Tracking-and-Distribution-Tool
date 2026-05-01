package com.ctdt.controller;
import com.ctdt.concurrency.ProjectLockManager;
import com.ctdt.dto.*;
import com.ctdt.exception.RecalculationLockException;
import com.ctdt.service.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
@RestController @RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService ps; private final ContributorService cs;
    private final RecalculationService rs; private final ProjectLockManager lm;
    public ProjectController(ProjectService ps,ContributorService cs,RecalculationService rs,ProjectLockManager lm){
        this.ps=ps;this.cs=cs;this.rs=rs;this.lm=lm;
    }
    @PostMapping
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody ProjectRequest req){
        return ResponseEntity.status(HttpStatus.CREATED).body(ps.create(req));
    }
    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAll(){return ResponseEntity.ok(ps.getAll());}

    @GetMapping("/find-by-path")
    public ResponseEntity<?> findByPath(@RequestParam String path){
        return ps.findByPath(path)
            .<ResponseEntity<?>>map(ResponseEntity::ok)
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message","No project found at this path.")));
    }

    /** Resolve a join token to project info (used by agent and join-by-link flow) */
    @GetMapping("/join/{token}")
    public ResponseEntity<?> resolveJoinToken(@PathVariable String token){
        try{return ResponseEntity.ok(ps.findByJoinToken(token));}
        catch(Exception e){return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message","Invalid join token."));}
    }

    @DeleteMapping("/{pid}")
    public ResponseEntity<Void> delete(@PathVariable Long pid){
        ps.delete(pid);
        return ResponseEntity.noContent().build();
    }

    /** Return all file logs for a project (for dashboard files panel) */
    @GetMapping("/{pid}/files")
    public ResponseEntity<List<FileEntryResponse>> getFiles(@PathVariable Long pid){
        return ResponseEntity.ok(ps.getFiles(pid));
    }

    @PostMapping("/{pid}/contributors")
    public ResponseEntity<ContributorResponse> addContrib(@PathVariable Long pid,@Valid @RequestBody ContributorRequest req){
        return ResponseEntity.status(HttpStatus.CREATED).body(cs.add(pid,req));
    }
    @GetMapping("/{pid}/contributors")
    public ResponseEntity<List<ContributorResponse>> getContribs(@PathVariable Long pid){
        return ResponseEntity.ok(cs.getAll(pid));
    }
    @PostMapping("/{pid}/recalculate")
    public ResponseEntity<?> recalculate(@PathVariable Long pid){
        try{return ResponseEntity.ok(rs.recalculate(pid));}
        catch(RecalculationLockException e){return ResponseEntity.status(429).body(Map.of("status",429,"message",e.getMessage()));}
    }
    @GetMapping("/{pid}/recalculate/status")
    public ResponseEntity<Map<String,Object>> status(@PathVariable Long pid){
        return ResponseEntity.ok(Map.of("projectId",pid,"locked",lm.isLocked(pid),"queueLength",lm.getQueueLength(pid)));
    }
    @GetMapping("/{pid}/results")
    public ResponseEntity<List<ResultResponse>> results(@PathVariable Long pid){return ResponseEntity.ok(rs.getResults(pid));}
    @GetMapping("/{pid}/recalculation-history")
    public ResponseEntity<List<RecalculationHistoryResponse>> history(@PathVariable Long pid,@RequestParam(defaultValue="0") int limit){
        return ResponseEntity.ok(limit>0?rs.getHistoryPaged(pid,limit):rs.getHistory(pid));
    }
}
