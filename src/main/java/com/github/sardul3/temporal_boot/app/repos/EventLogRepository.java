package com.github.sardul3.temporal_boot.app.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.sardul3.temporal_boot.common.models.EventLog;

import java.util.List;

@Repository
public interface EventLogRepository extends JpaRepository<EventLog, Long> {

    // Find all events by requestId
    List<EventLog> findByRequestRequestId(Long requestId);

    // Find events by type
    List<EventLog> findByEventType(String eventType);
}
