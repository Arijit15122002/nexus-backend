package com.arijit.nexus_backend.ai.agent.refactorer.service;

import com.arijit.nexus_backend.ai.agent.reviewer.dto.ReviewResult;
import com.arijit.nexus_backend.ai.provider.groq.service.GroqService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefactorerAgentService {

    private final GroqService groqService;

    private final RefactorerPromptBuilderService
            promptBuilderService;

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

        String response =
                groqService.generateResponse(
                        prompt
                );

        log.info(
                "\n================ REFACTORER RESPONSE ================\n{}\n====================================================",
                response
        );

        return response;

    }

}