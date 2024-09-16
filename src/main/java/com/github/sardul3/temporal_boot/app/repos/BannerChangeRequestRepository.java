package com.github.sardul3.temporal_boot.app.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.sardul3.temporal_boot.common.models.BannerChangeRequest;

import java.util.List;

@Repository
public interface BannerChangeRequestRepository extends JpaRepository<BannerChangeRequest, Long> {

    // Find all requests by status
    List<BannerChangeRequest> findByStatus(String status);

    // Find all requests for a specific item
    List<BannerChangeRequest> findByItemId(Long itemId);
}
