package com.github.sardul3.temporal_boot.common.activities;

import java.util.List;

import com.github.sardul3.temporal_boot.common.models.product.AIWorkflowOutput;
import com.github.sardul3.temporal_boot.common.models.product.AiRawResponse;
import com.github.sardul3.temporal_boot.common.models.product.EnrichedProductData;
import com.github.sardul3.temporal_boot.common.models.product.FileReference;
import com.github.sardul3.temporal_boot.common.models.product.GtinList;
import com.github.sardul3.temporal_boot.common.models.product.PromptRequest;
import com.github.sardul3.temporal_boot.common.models.product.PromptTemplateMetadata;
import com.github.sardul3.temporal_boot.common.models.product.UseCaseIdentifier;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface ProductDataImprovementActivities {

    GtinList extractGtinsFromCsv(FileReference csvFile);

    List<EnrichedProductData> enrichProductData(GtinList gtins);

    PromptTemplateMetadata getPromptTemplate(UseCaseIdentifier useCase);

    PromptRequest buildPrompt(PromptTemplateMetadata template, List<EnrichedProductData> enrichedData);

    AiRawResponse invokeAiModel(PromptRequest promptRequest);

    AIWorkflowOutput extractAiResponse(AiRawResponse response);
}
