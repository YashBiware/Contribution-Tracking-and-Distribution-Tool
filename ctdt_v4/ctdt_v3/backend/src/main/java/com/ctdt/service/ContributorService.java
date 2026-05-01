package com.ctdt.service;
import com.ctdt.dto.*;
import com.ctdt.entity.*;
import com.ctdt.exception.ResourceNotFoundException;
import com.ctdt.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
@Service
public class ContributorService {
    private final ContributorRepository cr;
    private final ProjectRepository pr;
    public ContributorService(ContributorRepository cr,ProjectRepository pr){this.cr=cr;this.pr=pr;}

    @Transactional
    public ContributorResponse add(Long pid, ContributorRequest req){
        Project p=pr.findById(pid).orElseThrow(()->new ResourceNotFoundException("Project not found: "+pid));
        Contributor c=new Contributor();
        c.setContributorName(req.getContributorName().trim());
        c.setProject(p);
        if(req.getUsername()!=null&&!req.getUsername().isBlank())
            c.setUsername(req.getUsername().trim().toLowerCase());
        if(req.getPassword()!=null&&!req.getPassword().isBlank())
            c.setPasswordHash(sha256(req.getPassword()));
        return toDto(cr.save(c));
    }

    @Transactional(readOnly=true)
    public List<ContributorResponse> getAll(Long pid){
        pr.findById(pid).orElseThrow(()->new ResourceNotFoundException("Project not found: "+pid));
        return cr.findByProject_ProjectId(pid).stream().map(this::toDto).toList();
    }

    public static String sha256(String input){
        try{
            MessageDigest md=MessageDigest.getInstance("SHA-256");
            byte[] bytes=md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb=new StringBuilder();
            for(byte b:bytes)sb.append(String.format("%02x",b));
            return sb.toString();
        }catch(Exception e){throw new RuntimeException(e);}
    }

    private ContributorResponse toDto(Contributor c){
        ContributorResponse r=new ContributorResponse();
        r.setContributorId(c.getContributorId());
        r.setContributorName(c.getContributorName());
        r.setUsername(c.getUsername());
        r.setProjectId(c.getProject().getProjectId());
        r.setJoinedAt(c.getJoinedAt());
        return r;
    }
}
