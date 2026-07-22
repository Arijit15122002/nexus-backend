package com.arijit.nexus_backend.ai.agent.reviewer.service;

import com.arijit.nexus_backend.ai.agent.reviewer.dto.ReviewResult;
import com.arijit.nexus_backend.ai.agent.reviewer.parser.ReviewResultParserService;
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
public class ReviewerAgentService {

    private final AIService aiService;

    private final ReviewerPromptBuilderService
            promptBuilderService;

    private final ReviewResultParserService
            reviewResultParserService;

    private final ReviewerLoggingService
            reviewerLoggingService;

    public ReviewResult review(

            String userRequest,

            String generatedProject

    ) {

        String prompt =
                promptBuilderService.buildPrompt(
                        userRequest,
                        generatedProject
                );

        AIRequest request = AIRequest.builder()

                .provider(AIProviderType.NVIDIA)

                .model("deepseek-ai/deepseek-v4-pro")

                .systemPrompt("""
                        You are ORKA's Principal Code Reviewer.

                        Review the generated project thoroughly.

                        Detect:

                        - Security issues
                        - Performance issues
                        - Architecture violations
                        - Missing files
                        - Bugs
                        - Incorrect implementations
                        - Best practice violations

                        Return ONLY valid JSON.

                        Do not return markdown.

                        Do not explain outside JSON.
                        """)

                .userPrompt(prompt)

                .temperature(1.0)

                .maxTokens(16384)

                .stream(false)

                .build();

        AIResponse aiResponse =
                aiService.generate(request);

        String response =
                aiResponse.getContent();

        log.info(
                "\n================ REVIEWER RAW RESPONSE ================\n{}\n====================================================",
                response
        );

        ReviewResult result =
                reviewResultParserService.parse(
                        response
                );

        reviewerLoggingService
                .logReviewResult(
                        result
                );

        return result;

    }

}