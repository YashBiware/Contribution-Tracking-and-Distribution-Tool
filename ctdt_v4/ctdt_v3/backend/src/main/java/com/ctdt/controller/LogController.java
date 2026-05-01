package com.ctdt.controller;
import com.ctdt.dto.*; import com.ctdt.service.ContributionLogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus; import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/logs")
public class LogController {
    private final ContributionLogService svc;
    public LogController(ContributionLogService s){svc=s;}
    @PostMapping public ResponseEntity<LogResponse> submit(@Valid @RequestBody LogRequest req){return ResponseEntity.status(HttpStatus.CREATED).body(svc.submit(req));}
}
