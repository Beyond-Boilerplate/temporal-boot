package com.github.sardul3.temporal_boot.api.dtos;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BannerChangeRequest {
    private String itemId;
    private String bannerMessage;
    private String itemCategory;
    private LocalDateTime applyDateTime;
}
