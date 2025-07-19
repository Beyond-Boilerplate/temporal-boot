package com.github.sardul3.temporal_boot.common.models.product;

import java.math.BigDecimal;
import java.util.Map;

public record EnrichedProductData(
    String gtin,
    String productName,
    String brand,
    String category,
    String subcategory,
    BigDecimal price,
    Map<String, String> additionalAttributes
) {}

