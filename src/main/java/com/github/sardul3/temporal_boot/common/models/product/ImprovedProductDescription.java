package com.github.sardul3.temporal_boot.common.models.product;

import java.util.Map;

public record ImprovedProductDescription(
    String gtin,
    String improvedText,
    String format, // e.g., "markdown", "html"
    Map<String, String> metadata
) {} 