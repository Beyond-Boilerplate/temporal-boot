package com.github.sardul3.temporal_boot.common.workflows;

import com.github.sardul3.temporal_boot.common.models.product.*;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

import java.util.List;

@WorkflowInterface
public interface ProductDataImprovementWorkflow {

     /**
     * Orchestrates the full data improvement flow:
     *  - Read GTINs from CSV
     *  - Enrich product data
     *  - Retrieve prompt template
     *  - Build prompt
     *  - Call AI model
     *  - Extract structured output
     */
    @WorkflowMethod
    AIWorkflowOutput runImprovementWorkflow(FileReference csvFile, UseCaseIdentifier useCase);

}
