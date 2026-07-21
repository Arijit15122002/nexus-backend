package com.arijit.nexus_backend.ai.agent.developer.service;

import com.arijit.nexus_backend.ai.agent.architect.dto.ArchitecturePlan;
import com.arijit.nexus_backend.ai.agent.developer.prompt.DeveloperPromptBuilderService;
import com.arijit.nexus_backend.ai.provider.deepseek.service.DeepSeekService;
import com.arijit.nexus_backend.ai.provider.gemini.service.GeminiService;
import com.arijit.nexus_backend.ai.provider.groq.service.GroqService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeveloperAgentService {

    private final GroqService groqService;

    private final DeveloperPromptBuilderService
            promptBuilderService;

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

        String response =
                groqService.generateResponse(
                        prompt
                );

        log.info(
                "\n================ DEVELOPER RESPONSE ================\n{}\n====================================================",
                response
        );

        return response;

    }

}