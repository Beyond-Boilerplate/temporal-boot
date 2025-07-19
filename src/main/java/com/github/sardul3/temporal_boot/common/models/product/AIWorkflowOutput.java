package com.github.sardul3.temporal_boot.common.models.product;

import java.util.List;

public record AIWorkflowOutput(
    String useCase,
    List<ImprovedProductDescription> results
) {}