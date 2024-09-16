package com.github.sardul3.temporal_boot.common.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Entity
@Table(name = "event_logs")
@Getter
@Setter
public class EventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    @ManyToOne
    @JoinColumn(name = "request_id", nullable = false)
    private BannerChangeRequest request;

    @Column(nullable = false)
    private String eventType;  // Created, ReminderSent, Applied, Rejected, etc.

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column
    private String eventDetails;  // Additional details about the event
}

