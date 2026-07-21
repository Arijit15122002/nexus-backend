package com.arijit.nexus_backend.ai.executor.service;

import com.arijit.nexus_backend.ai.agent.developer.service.DeveloperAgentService;
import com.arijit.nexus_backend.ai.agent.refactorer.service.RefactorerAgentService;
import com.arijit.nexus_backend.ai.agent.reviewer.dto.ReviewResult;
import com.arijit.nexus_backend.ai.agent.reviewer.service.ReviewerAgentService;
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
import com.arijit.nexus_backend.ai.tool.entity.ToolDomain;
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

    private final DeveloperAgentService developerAgentService;

    private final ReviewerAgentService reviewerAgentService;

    private final RefactorerAgentService
            refactorerAgentService;

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

        String rawResponse;

        if (
                context.getArchitecturePlan()
                        != null
        ) {

            rawResponse =
                    developerAgentService.generateProject(

                            context.getUserMessage(),

                            context.getArchitecturePlan()

                    );

        }
        else {
            System.out.println("========== MAIN GROQ GENERATION ==========");
            rawResponse =
                    groqService.generateResponse(
                            context.getFinalPrompt()
                    );

        }

        ReviewResult reviewResult = null;

        if (
                context.getDomain()
                        == ToolDomain.CODE
                        &&
                        context.getArchitecturePlan()
                                != null
        ) {

            reviewResult =
                    reviewerAgentService.review(

                            context.getUserMessage(),

                            rawResponse

                    );

            if (reviewResult != null) {

                System.out.println(
                        "\n================ REVIEW SUMMARY ================\n"
                );

                System.out.println(
                        "SCORE = " +
                                reviewResult.getScore()
                );

                System.out.println(
                        "ISSUES = " +
                                reviewResult.getIssues().size()
                );

                System.out.println(
                        "\n================================================\n"
                );

            }

            if (
                    reviewResult != null
                            &&
                            reviewResult.getScore() < 90
            ) {

                System.out.println(
                        "\n================ REFACTORING PROJECT ================\n"
                );

                rawResponse =
                        refactorerAgentService.refactor(

                                context.getUserMessage(),

                                rawResponse,

                                reviewResult

                        );

                System.out.println(
                        "\n================ REFACTORING COMPLETE ================\n"
                );

            }

        }

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
        return context.getDomain() == ToolDomain.CODE;
    }
}