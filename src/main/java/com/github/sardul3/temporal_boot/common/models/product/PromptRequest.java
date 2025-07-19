package com.github.sardul3.temporal_boot.common.models.product;

import java.util.List;

public record PromptRequest(
    String finalPrompt,
    String useCase,
    List<EnrichedProductData> productData,
    String model,
    String provider
) {}

