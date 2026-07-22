package com.arijit.nexus_backend.ai.agent.refactorer.service;

import com.arijit.nexus_backend.ai.agent.reviewer.dto.ReviewResult;
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
public class RefactorerAgentService {

    private final AIService aiService;

    private final RefactorerPromptBuilderService promptBuilderService;

    public String refactor(

            String userRequest,

            String generatedProject,

            ReviewResult reviewResult

    ) {

        String prompt =
                promptBuilderService.buildPrompt(
                        userRequest,
                        generatedProject,
                        reviewResult
                );

        log.info(
                "\n================ REFACTORER PROMPT ================\n{}\n====================================================",
                prompt
        );

        AIRequest request = AIRequest.builder()

                .provider(AIProviderType.NVIDIA)

                .model("openai/gpt-oss-120b")

                .systemPrompt("""
                        You are a Principal Software Refactoring Engineer.

                        Improve the implementation without changing the architecture.

                        Return ONLY source files.

                        The first line MUST begin with:

                        FILE:

                        Do not return markdown explanations.
                        """)

                .userPrompt(prompt)

                .temperature(0.7)

                .maxTokens(16384)

                .stream(false)

                .build();

        AIResponse aiResponse =
                aiService.generate(request);

        String response =
                aiResponse.getContent();

        log.info(
                "\n================ REFACTORER RESPONSE ================\n{}\n====================================================",
                response
        );

        return response;

    }

}