package com.github.sardul3.temporal_boot.common.models.product;

import java.util.Map;

public record PromptTemplateMetadata(
    String useCase,
    String promptTemplate,
    String model,
    String provider,
    Map<String, String> metadata
) {}

