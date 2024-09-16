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

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;


@Entity
@Table(name = "requests")
@Getter
@Setter
public class BannerChangeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @Column(nullable = false)
    private Long itemId;

    @Column(nullable = false)
    private String bannerName;

    @Column(nullable = false)
    private LocalDateTime applyDateTime;

    @Column(nullable = false)
    private boolean approvalRequired;

    @Column(nullable = true)
    private String approvalStatus;  // Pending, Approved, Rejected

    @Column(nullable = true)
    private String status;  // Pending, Applied, Rejected

    @Column(nullable = true)
    private String priority;  // Low, Medium, High

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
