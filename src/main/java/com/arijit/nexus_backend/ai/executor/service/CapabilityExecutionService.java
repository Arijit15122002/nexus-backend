package com.arijit.nexus_backend.ai.executor.service;

import com.arijit.nexus_backend.ai.artifact.dto.CodeArtifact;
import com.arijit.nexus_backend.ai.artifact.service.ArtifactExtractionService;
import com.arijit.nexus_backend.ai.artifact.structure.ProjectStructureBuilderService;
import com.arijit.nexus_backend.ai.artifact.structure.ProjectStructureNode;
import com.arijit.nexus_backend.ai.executor.dto.ExecutionContext;
import com.arijit.nexus_backend.ai.executor.entity.ExecutionMode;
import com.arijit.nexus_backend.ai.provider.groq.service.GroqService;
import com.arijit.nexus_backend.ai.response.dto.ResponseSection;
import com.arijit.nexus_backend.ai.response.parser.service.ParserRoutingService;
import com.arijit.nexus_backend.ai.stream.dto.StreamingChunk;
import com.arijit.nexus_backend.ai.stream.service.StreamingResponseBuilderService;
import com.arijit.nexus_backend.ai.tool.entity.ToolCapability;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CapabilityExecutionService {

    private final GroqService groqService;

    private final ParserRoutingService parserRoutingService;

    private final ArtifactExtractionService artifactExtractionService;

    private final ProjectStructureBuilderService
            projectStructureBuilderService;

    private final StreamingResponseBuilderService
            streamingResponseBuilderService;

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

    public Flux<StreamingChunk> execute(
            ExecutionContext context
    ) {

        String rawResponse =
                groqService.generateResponse(
                        context.getFinalPrompt()
                );

        rawResponse =
                responsePostProcessingService
                        .processResponse(
                                rawResponse
                        );

        System.out.println(
                "\n================ RAW GROQ RESPONSE ================\n"
        );

        System.out.println(rawResponse);

        System.out.println(
                "\n===================================================\n"
        );

        List<ResponseSection> sections =
                parserRoutingService.parse(
                        context.getDomain(),
                        rawResponse
                );

        System.out.println(
                "DOMAIN = "
                        + context.getDomain()
        );

        System.out.println(
                "SECTIONS = "
                        + sections.size()
        );

        List<CodeArtifact> artifacts;

        if (
                shouldGenerateArtifacts(
                        context,
                        rawResponse
                )
        ) {

            artifacts =
                    artifactExtractionService.extractArtifacts(
                            sections
                    );

        }
        else {

            artifacts = List.of();

        }

        System.out.println(
                "ARTIFACTS = "
                        + artifacts.size()
        );

        ProjectStructureNode structure =
                projectStructureBuilderService.buildStructure(
                        artifacts
                );

        return streamingResponseBuilderService.build(
                rawResponse,
                sections,
                artifacts,
                structure
        );

    }

    private boolean shouldGenerateArtifacts(

            ExecutionContext context,

            String rawResponse

    ) {

        if (
                context.getDomain()
                        != com.arijit.nexus_backend.ai.tool.entity.ToolDomain.CODE
        ) {

            return false;

        }

        long fileCount =
                rawResponse.lines()

                        .filter(
                                line ->
                                        line.trim()
                                                .startsWith("FILE:")
                        )

                        .count();

        return fileCount > 0;

    }

}