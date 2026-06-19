package com.arijit.nexus_backend.ai.agent.architect.service;

import com.arijit.nexus_backend.ai.agent.architect.dto.ArchitecturePlan;
import com.arijit.nexus_backend.ai.agent.architect.parser.ArchitecturePlanParserService;
import com.arijit.nexus_backend.ai.provider.groq.service.GroqService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArchitectAgentService {

    private final GroqService groqService;

    private final ArchitectPromptBuilderService
            promptBuilderService;

    private final ArchitecturePlanParserService
            parserService;

    public ArchitecturePlan createPlan(
            String userRequest
    ) {

        String prompt =
                promptBuilderService.buildPrompt(
                        userRequest
                );

        String response =
                groqService.generateResponse(
                        prompt
                );

//        log.info(
//                "\n================ ARCHITECT RAW RESPONSE ================\n{}\n========================================================",
//                response
//        );


        log.info(
                "ARCHITECT RESPONSE:\n{}",
                response
        );

        ArchitecturePlan plan =
                parserService.parse(
                        response
                );

        log.info(
                "\n================ PARSED ARCHITECT PLAN ================\n{}\n=======================================================",
                plan
        );

        return plan;

    }

}