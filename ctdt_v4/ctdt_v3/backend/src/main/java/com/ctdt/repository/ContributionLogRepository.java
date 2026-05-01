package com.ctdt.repository;
import com.ctdt.entity.ContributionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
public interface ContributionLogRepository extends JpaRepository<ContributionLog, Long> {
    @Query("SELECT l FROM ContributionLog l JOIN FETCH l.contributor WHERE l.project.projectId = :pid AND l.retainedFlag = true")
    List<ContributionLog> findRetainedByProject(@Param("pid") Long pid);

    @Query("SELECT COUNT(l) FROM ContributionLog l WHERE l.project.projectId = :pid")
    Long countProjectLogs(@Param("pid") Long pid);

    @Query("SELECT l FROM ContributionLog l JOIN FETCH l.contributor WHERE l.project.projectId = :pid ORDER BY l.timestamp DESC")
    List<ContributionLog> findAllByProjectOrderByTimestampDesc(@Param("pid") Long pid);
}
