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

import org.springframework.data.annotation.CreatedDate;

@Entity
@Table(name = "audit_trails")
@Getter
@Setter
public class AuditTrail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auditId;

    @ManyToOne
    @JoinColumn(name = "request_id", nullable = false)
    private BannerChangeRequest request;

    @Column(nullable = false)
    private String action;  // Created, Approved, Rejected, Applied

    @Column(nullable = false)
    private String performedBy;

    @CreatedDate
    private LocalDateTime timestamp;

    @Column(nullable = true)
    private String details;  // Optional metadata for more information
}

