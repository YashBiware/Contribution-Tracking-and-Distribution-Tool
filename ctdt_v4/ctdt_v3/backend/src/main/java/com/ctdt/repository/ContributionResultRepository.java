package com.ctdt.repository;
import com.ctdt.entity.ContributionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface ContributionResultRepository extends JpaRepository<ContributionResult,Long> {
    List<ContributionResult> findByProject_ProjectIdOrderByPercentageDesc(Long projectId);    Optional<ContributionResult> findByContributor_ContributorIdAndProject_ProjectId(Long cid, Long pid);
}
