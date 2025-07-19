package com.github.sardul3.temporal_boot.common.activities;

import com.github.sardul3.temporal_boot.common.models.product.AIWorkflowOutput;
import com.github.sardul3.temporal_boot.common.models.product.AiRawResponse;
import com.github.sardul3.temporal_boot.common.models.product.EnrichedProductData;
import com.github.sardul3.temporal_boot.common.models.product.FileReference;
import com.github.sardul3.temporal_boot.common.models.product.GtinList;
import com.github.sardul3.temporal_boot.common.models.product.ImprovedProductDescription;
import com.github.sardul3.temporal_boot.common.models.product.PromptRequest;
import com.github.sardul3.temporal_boot.common.models.product.PromptTemplateMetadata;
import com.github.sardul3.temporal_boot.common.models.product.UseCaseIdentifier;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class ProductDataImprovementActivitiesImpl implements ProductDataImprovementActivities {


    @Override
    public GtinList extractGtinsFromCsv(FileReference csvFile) {
        List<String> gtins = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile.path()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Assumes GTINs are in the first column, skip header
                if (line.toLowerCase().contains("gtin")) continue;
                String[] parts = line.split(",");
                if (parts.length > 0) {
                    gtins.add(parts[0].trim());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read GTINs from CSV", e);
        }
        return new GtinList(gtins);
    }

    @Override
    public List<EnrichedProductData> enrichProductData(GtinList gtins) {
        // Mock implementation for enrichment
        return gtins.gtins().stream().map(gtin ->
            new EnrichedProductData(
                gtin,
                "Product " + gtin,
                "BrandX",
                "Electronics",
                "Smartphones",
                new BigDecimal("499.99"),
                Map.of("color", "black", "memory", "128GB")
            )
        ).collect(Collectors.toList());
    }

    @Override
    public PromptTemplateMetadata getPromptTemplate(UseCaseIdentifier useCase) {
        // Simulate loading prompt template from file or database
        String prompt = """
            You are a creative marketing assistant. Generate a premium product description for:
            
            Product: {{productName}}
            Brand: {{brand}}
            Category: {{category}}
            Price: {{price}}
            
            Emphasize uniqueness and call to action.
        """;
        return new PromptTemplateMetadata(
            useCase.name(),
            prompt,
            "gpt-4",
            "openai",
            Map.of("temperature", "0.7", "format", "markdown")
        );
    }

    @Override
    public PromptRequest buildPrompt(PromptTemplateMetadata template, List<EnrichedProductData> enrichedData) {
        StringBuilder finalPrompt = new StringBuilder();
        for (EnrichedProductData product : enrichedData) {
            String rendered = template.promptTemplate()
                .replace("{{productName}}", product.productName())
                .replace("{{brand}}", product.brand())
                .replace("{{category}}", product.category())
                .replace("{{price}}", "$" + product.price().toPlainString());

            finalPrompt.append(rendered).append("\n\n");
        }

        return new PromptRequest(
            finalPrompt.toString().trim(),
            template.useCase(),
            enrichedData,
            template.model(),
            template.provider()
        );
    }

    @Override
    public AiRawResponse invokeAiModel(PromptRequest promptRequest) {
        // Simulate OpenAI/Gemini/Claude API call
        String fakeResponse = promptRequest.productData().stream()
            .map(p -> "Improved description for " + p.productName())
            .collect(Collectors.joining("\n\n"));

        return new AiRawResponse(fakeResponse, 500, promptRequest.model());
    }

    @Override
    public AIWorkflowOutput extractAiResponse(AiRawResponse response) {
        // Split by double newline to simulate multiple outputs
        String[] lines = response.rawJson().split("\n\n");

        List<ImprovedProductDescription> descriptions = new ArrayList<>();
        for (int i = 0; i < lines.length; i++) {
            descriptions.add(new ImprovedProductDescription(
                "GTIN-" + (i + 1),
                lines[i],
                "markdown",
                Map.of("source", "AI")
            ));
        }

        return new AIWorkflowOutput("product-description", descriptions);
    }
}

