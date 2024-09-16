package com.github.sardul3.temporal_boot.app.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.sardul3.temporal_boot.common.models.AuditTrail;

import java.util.List;

@Repository
public interface AuditTrailRepository extends JpaRepository<AuditTrail, Long> {

    // Find all audit logs by requestId
    List<AuditTrail> findByRequestRequestId(Long requestId);
}
