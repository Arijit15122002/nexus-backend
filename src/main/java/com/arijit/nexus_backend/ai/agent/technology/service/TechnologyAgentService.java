package com.arijit.nexus_backend.ai.agent.technology.service;

import com.arijit.nexus_backend.ai.agent.architect.dto.ArchitecturePlan;
import com.arijit.nexus_backend.ai.agent.technology.dto.TechnologyReviewResult;
import com.arijit.nexus_backend.ai.agent.technology.parser.TechnologyResultParserService;
import com.arijit.nexus_backend.ai.provider.groq.service.GroqService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TechnologyAgentService {

    private final GroqService groqService;

    private final TechnologyPromptBuilderService
            promptBuilderService;

    private final TechnologyResultParserService
            parserService;

    public TechnologyReviewResult review(
            ArchitecturePlan plan
    ) {

        String prompt =
                promptBuilderService.buildPrompt(
                        plan
                );

        String response =
                groqService.generateResponse(
                        prompt
                );

        System.out.println(
                "\n=========== TECHNOLOGY RESPONSE ===========\n"
        );

        System.out.println(response);

        System.out.println(
                "\n===========================================\n"
        );

        return parserService.parse(
                response
        );

    }

}