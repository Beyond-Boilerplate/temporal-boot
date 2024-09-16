package com.github.sardul3.temporal_boot.app.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.sardul3.temporal_boot.common.models.CustomerRequestStatus;

import java.util.List;

@Repository
public interface CustomerRequestStatusRepository extends JpaRepository<CustomerRequestStatus, Long> {

    // Find status by requestId
    CustomerRequestStatus findByRequestId(Long requestId);

    // Find all statuses by customerId
    List<CustomerRequestStatus> findByCustomerId(Long customerId);
}
