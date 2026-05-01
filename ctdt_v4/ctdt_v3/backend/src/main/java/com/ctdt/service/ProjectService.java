package com.ctdt.service;
import com.ctdt.dto.*;
import com.ctdt.entity.Project;
import com.ctdt.entity.ContributionLog;
import com.ctdt.exception.BadRequestException;
import com.ctdt.exception.ResourceNotFoundException;
import com.ctdt.repository.ContributionLogRepository;
import com.ctdt.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
@Service
public class ProjectService {
    private final ProjectRepository repo;
    private final ContributionLogRepository logRepo;
    public ProjectService(ProjectRepository r, ContributionLogRepository lr){repo=r;logRepo=lr;}

    @Transactional
    public ProjectResponse create(ProjectRequest req){
        Project p=new Project();
        p.setProjectName(req.getProjectName().trim());
        if(req.getDirectoryPath()!=null&&!req.getDirectoryPath().isBlank())
            p.setDirectoryPath(req.getDirectoryPath().trim());
        return toDto(repo.save(p));
    }

    @Transactional(readOnly=true)
    public List<ProjectResponse> getAll(){
        return repo.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly=true)
    public Optional<ProjectResponse> findByPath(String path){
        return repo.findByDirectoryPath(path).map(this::toDto);
    }

    @Transactional(readOnly=true)
    public ProjectResponse findByJoinToken(String token){
        return repo.findByJoinToken(token)
            .map(this::toDto)
            .orElseThrow(()->new ResourceNotFoundException("Invalid join token."));
    }

    @Transactional
    public void delete(Long pid){
        Project p=repo.findById(pid)
            .orElseThrow(()->new ResourceNotFoundException("Project not found: "+pid));
        repo.delete(p);
    }

    @Transactional(readOnly=true)
    public List<FileEntryResponse> getFiles(Long pid){
        repo.findById(pid).orElseThrow(()->new ResourceNotFoundException("Project not found: "+pid));
        return logRepo.findAllByProjectOrderByTimestampDesc(pid)
            .stream().map(this::toFileDto).toList();
    }

    private FileEntryResponse toFileDto(ContributionLog l){
        FileEntryResponse r=new FileEntryResponse();
        r.setLogId(l.getLogId());
        r.setContributorId(l.getContributor().getContributorId());
        r.setContributorName(l.getContributor().getContributorName());
        r.setFileName(l.getFileName());
        r.setContentSize(l.getContentSize());
        r.setWeightFactor(l.getWeightFactor());
        r.setContributionType(l.getContributionType().name());
        r.setRetainedFlag(l.getRetainedFlag());
        r.setTimestamp(l.getTimestamp());
        return r;
    }

    private ProjectResponse toDto(Project p){
        ProjectResponse r=new ProjectResponse();
        r.setProjectId(p.getProjectId());
        r.setProjectName(p.getProjectName());
        r.setDirectoryPath(p.getDirectoryPath());
        r.setJoinToken(p.getJoinToken());
        r.setStatus(p.getStatus().name());
        r.setCreatedAt(p.getCreatedAt());
        return r;
    }
}
