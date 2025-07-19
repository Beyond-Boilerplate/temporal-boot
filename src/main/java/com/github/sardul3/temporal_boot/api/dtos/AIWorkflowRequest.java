package com.github.sardul3.temporal_boot.api.dtos;

import com.github.sardul3.temporal_boot.common.models.product.FileReference;
import com.github.sardul3.temporal_boot.common.models.product.UseCaseIdentifier;
import lombok.Data;

@Data
public class AIWorkflowRequest {
    private FileReference fileReference;
    private UseCaseIdentifier useCase;
}
