package com.github.sardul3.temporal_boot.app.repos;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.sardul3.temporal_boot.common.models.CurrentBanner;

@Repository
public interface CurrentBannerRepository extends JpaRepository<CurrentBanner, Long> {

    // Find the current banner for an item
    CurrentBanner findByItemId(Long itemId);
}
