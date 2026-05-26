package com.arijit.nexus_backend.ai.executor.service;

import com.arijit.nexus_backend.ai.executor.dto.ExecutionContext;
import com.arijit.nexus_backend.ai.executor.entity.ExecutionMode;
import com.arijit.nexus_backend.ai.provider.groq.service.GroqService;
import com.arijit.nexus_backend.ai.tool.entity.ToolCapability;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class CapabilityExecutionService {

    private final GroqService groqService;

    private final ResponsePostProcessingService
            responsePostProcessingService;

    public ExecutionMode resolveExecutionMode(
            ToolCapability capability
    ) {

        return switch (capability) {

            case CODE_GENERATION,
                 API_GENERATION,
                 DATABASE_DESIGN,
                 TEST_GENERATION,
                 REFACTORING

                    -> ExecutionMode.CODE_MODE;

            case BUG_FIXING

                    -> ExecutionMode.DEBUG_MODE;

            case ARCHITECTURE_DESIGN,
                 SYSTEM_DESIGN,
                 RAG_ARCHITECTURE

                    -> ExecutionMode.ARCHITECTURE_MODE;

            case ROADMAP_GENERATION,
                 LEARNING_GUIDANCE,
                 INTERVIEW_PREPARATION

                    -> ExecutionMode.ROADMAP_MODE;

            case WEB_RESEARCH,
                 COMPARISON_ANALYSIS,
                 TECH_STACK_RECOMMENDATION

                    -> ExecutionMode.RESEARCH_MODE;

            case RESUME_GENERATION,
                 COVER_LETTER_GENERATION,
                 DOCUMENT_SUMMARIZATION

                    -> ExecutionMode.DOCUMENT_MODE;

            case AI_AGENT_DESIGN,
                 PROMPT_ENGINEERING

                    -> ExecutionMode.AI_ENGINEERING_MODE;

            default -> ExecutionMode.NORMAL_CHAT;

        };

    }

    public Flux<String> execute(
            ExecutionContext context
    ) {

        return groqService
                .generateResponseStream(
                        context.getFinalPrompt()
                );

    }

}