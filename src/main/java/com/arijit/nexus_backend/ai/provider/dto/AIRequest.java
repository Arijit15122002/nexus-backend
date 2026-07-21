package com.arijit.nexus_backend.ai.provider.dto;

import com.arijit.nexus_backend.ai.provider.model.AIProviderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIRequest {

    private AIProviderType provider;

    /**
     * Exact model name.
     *
     * Example:
     * llama-3.3-70b-versatile
     * nvidia/nemotron-3-nano-omni-30b-a3b-reasoning
     */
    private String model;

    private String systemPrompt;

    private String userPrompt;

    @Builder.Default
    private Double temperature = 0.2;

    @Builder.Default
    private Integer maxTokens = 8192;

    @Builder.Default
    private Boolean stream = false;

}