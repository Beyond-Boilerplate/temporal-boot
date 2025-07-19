package com.github.sardul3.temporal_boot.common.workflows;

import java.util.List;
import java.util.Map;

import com.github.sardul3.temporal_boot.common.activities.ProductDataImprovementActivities;
import com.github.sardul3.temporal_boot.common.models.product.AIWorkflowOutput;
import com.github.sardul3.temporal_boot.common.models.product.AiRawResponse;
import com.github.sardul3.temporal_boot.common.models.product.EnrichedProductData;
import com.github.sardul3.temporal_boot.common.models.product.FileReference;
import com.github.sardul3.temporal_boot.common.models.product.GtinList;
import com.github.sardul3.temporal_boot.common.models.product.PromptRequest;
import com.github.sardul3.temporal_boot.common.models.product.PromptTemplateMetadata;
import com.github.sardul3.temporal_boot.common.models.product.UseCaseIdentifier;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;

public class ProductDataImprovementWorkflowImpl implements ProductDataImprovementWorkflow {

    private final ProductDataImprovementActivities activities;

    public ProductDataImprovementWorkflowImpl(Map<String, ActivityOptions> activityOptionsMap) {
        this.activities = Workflow.newActivityStub(ProductDataImprovementActivities.class, 
                activityOptionsMap.get("productDataImprovementActivities"));
    }

    @Override
    public AIWorkflowOutput runImprovementWorkflow(FileReference csvFile, UseCaseIdentifier useCase) {
        GtinList gtins = activities.extractGtinsFromCsv(csvFile);
        List<EnrichedProductData> enriched = activities.enrichProductData(gtins);
        PromptTemplateMetadata template = activities.getPromptTemplate(useCase);
        PromptRequest promptRequest = activities.buildPrompt(template, enriched);
        AiRawResponse response = activities.invokeAiModel(promptRequest);
        return activities.extractAiResponse(response);
    }
}
