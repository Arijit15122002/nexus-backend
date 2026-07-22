package com.arijit.nexus_backend.ai.agent.developer.service;

import com.arijit.nexus_backend.ai.agent.architect.dto.ArchitecturePlan;
import com.arijit.nexus_backend.ai.agent.developer.prompt.DeveloperPromptBuilderService;
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
public class DeveloperAgentService {

    private final AIService aiService;

    private final DeveloperPromptBuilderService promptBuilderService;

    public String generateProject(
            String userRequest,
            ArchitecturePlan architecturePlan
    ) {

        String prompt =
                promptBuilderService.buildPrompt(
                        userRequest,
                        architecturePlan
                );

        log.info(
                "\n================ DEVELOPER PROMPT ================\n{}\n===================================================",
                prompt
        );

        AIRequest request = AIRequest.builder()

                .provider(AIProviderType.NVIDIA)

                .model("openai/gpt-oss-120b")

                .systemPrompt("""
                        You are ORKA's Principal Software Engineer.

                        Generate complete production-ready projects.

                        Follow the output contract EXACTLY.

                        The first line MUST begin with:

                        FILE:

                        Do not output markdown explanations.

                        Return only the generated files.
                        """)

                .userPrompt(prompt)

                .temperature(0.7)

                .maxTokens(2048)

                .stream(false)

                .build();

        log.info("Developer prompt characters: {}", prompt.length());

        AIResponse aiResponse =
                aiService.generate(request);

        String response =
                aiResponse.getContent();

        log.info(
                "\n================ DEVELOPER RESPONSE ================\n{}\n====================================================",
                response
        );

        return response;
    }
}