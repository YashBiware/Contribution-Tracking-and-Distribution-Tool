package com.ctdt.repository;
import com.ctdt.entity.Project;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
public interface ProjectRepository extends JpaRepository<Project,Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Project p WHERE p.projectId=:id")
    Optional<Project> findByIdForUpdate(@Param("id") Long id);

    @Query("SELECT p FROM Project p WHERE p.directoryPath=:path")
    Optional<Project> findByDirectoryPath(@Param("path") String path);

    @Query("SELECT p FROM Project p WHERE p.joinToken=:token")
    Optional<Project> findByJoinToken(@Param("token") String token);
}
