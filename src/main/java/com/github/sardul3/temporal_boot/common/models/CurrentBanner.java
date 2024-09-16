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

import org.springframework.data.annotation.LastModifiedDate;


@Entity
@Table(name = "current_banner_names")
@Getter
@Setter
public class CurrentBanner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    @Column(nullable = false)
    private String currentBannerName;

    @Column(nullable = false)
    private boolean active;

    @LastModifiedDate
    private LocalDateTime lastUpdated;
}

