package com.github.sardul3.temporal_boot.app.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.sardul3.temporal_boot.common.models.Approval;

import java.util.List;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval, Long> {

    // Find approvals by requestId
    List<Approval> findByRequestRequestId(Long requestId);

    // Find approvers for a given item category
    List<Approval> findByItemCategory(String itemCategory);
}
