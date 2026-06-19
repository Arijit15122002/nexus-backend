package com.arijit.nexus_backend.ai.agent.reviewer.service;

import com.arijit.nexus_backend.ai.agent.reviewer.dto.ReviewResult;
import com.arijit.nexus_backend.ai.agent.reviewer.parser.ReviewResultParserService;
import com.arijit.nexus_backend.ai.provider.groq.service.GroqService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewerAgentService {

    private final GroqService groqService;

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

//        log.info(
//                "\n================ REVIEWER PROMPT ================\n{}\n==================================================",
//                prompt
//        );

        String response =
                groqService.generateResponse(
                        prompt
                );

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