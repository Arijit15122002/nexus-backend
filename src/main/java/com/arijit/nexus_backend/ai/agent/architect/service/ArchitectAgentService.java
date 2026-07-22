package com.arijit.nexus_backend.ai.agent.architect.service;

import com.arijit.nexus_backend.ai.agent.architect.dto.ArchitecturePlan;
import com.arijit.nexus_backend.ai.agent.architect.parser.ArchitecturePlanParserService;
import com.arijit.nexus_backend.ai.provider.dto.AIRequest;
import com.arijit.nexus_backend.ai.provider.dto.AIResponse;
import com.arijit.nexus_backend.ai.provider.model.AIProviderType;
import com.arijit.nexus_backend.ai.provider.service.AIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArchitectAgentService {

    private final AIService aiService;

    private final ArchitectPromptBuilderService promptBuilderService;

    private final ArchitecturePlanParserService parserService;

    public ArchitecturePlan createPlan(String userRequest) {

        String prompt =
                promptBuilderService.buildPrompt(userRequest);

        AIRequest request = AIRequest.builder()

                .provider(AIProviderType.NVIDIA)

                .model("openai/gpt-oss-120b")

                .systemPrompt("""
                        You are a Principal Software Architect.

                        Design production-grade software architectures.

                        Return ONLY valid JSON.

                        Do not return markdown.
                        """)

                .userPrompt(prompt)

                .temperature(1.0)

                .maxTokens(4096)

                .stream(false)

                .build();

        AIResponse response =
                aiService.generate(request);

        String rawResponse =
                response.getContent();

        log.info(
                "ARCHITECT RESPONSE:\n{}",
                rawResponse
        );

        ArchitecturePlan plan =
                parserService.parse(rawResponse);

        log.info(
                "\n================ PARSED ARCHITECT PLAN ================\n{}\n=======================================================",
                plan
        );

        return plan;
    }
}