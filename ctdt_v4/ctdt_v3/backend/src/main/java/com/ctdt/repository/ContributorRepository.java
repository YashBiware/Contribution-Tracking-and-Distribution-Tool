package com.ctdt.repository;
import com.ctdt.entity.Contributor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
public interface ContributorRepository extends JpaRepository<Contributor,Long> {
    List<Contributor> findByProject_ProjectId(Long projectId);

    @Query("SELECT c FROM Contributor c WHERE c.username=:u AND c.project.projectId=:pid")
    Optional<Contributor> findByUsernameAndProject(@Param("u") String username, @Param("pid") Long projectId);
}
