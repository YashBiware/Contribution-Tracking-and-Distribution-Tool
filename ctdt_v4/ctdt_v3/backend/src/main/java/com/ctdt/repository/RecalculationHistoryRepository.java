package com.ctdt.repository;
import com.ctdt.entity.RecalculationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
public interface RecalculationHistoryRepository extends JpaRepository<RecalculationHistory,Long> {
    List<RecalculationHistory> findByProject_ProjectIdOrderByTriggeredAtDesc(Long projectId);
    @Query("SELECT h FROM RecalculationHistory h WHERE h.project.projectId=:pid ORDER BY h.triggeredAt DESC LIMIT :lim")
    List<RecalculationHistory> findRecentByProjectId(@Param("pid") Long pid, @Param("lim") int lim);
}
