package com.github.sardul3.temporal_boot.common.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;

@Entity
@Table(name = "customer_request_status")
@Getter
@Setter
public class CustomerRequestStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false)
    private String status;  // Pending, Completed, Rejected

    @LastModifiedBy
    private LocalDateTime lastUpdated;

    @CreatedDate
    private LocalDateTime createdAt;
}
