package com.arijit.nexus_backend.ai.response.factory;

import com.arijit.nexus_backend.ai.artifact.dto.CodeArtifact;
import com.arijit.nexus_backend.ai.artifact.dto.ProjectArtifact;
import com.arijit.nexus_backend.ai.artifact.service.ArtifactAssemblyService;
import com.arijit.nexus_backend.ai.artifact.service.ArtifactExtractionService;
import com.arijit.nexus_backend.ai.response.dto.AIResponse;
import com.arijit.nexus_backend.ai.response.dto.AIResponseMetadata;
import com.arijit.nexus_backend.ai.response.dto.ResponseSection;
import com.arijit.nexus_backend.ai.response.entity.SectionType;
import com.arijit.nexus_backend.ai.tool.entity.ToolCapability;
import com.arijit.nexus_backend.ai.tool.entity.ToolDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CodeResponseFactory {

    private final ArtifactExtractionService
            artifactExtractionService;

    private final ArtifactAssemblyService
            artifactAssemblyService;

    public AIResponse build(

            ToolCapability capability,

            String rawResponse,

            List<ResponseSection> sections

    ) {

        Map<String, Object> metadata =
                buildMetadata(sections);

        List<CodeArtifact> artifacts =
                artifactExtractionService
                        .extractArtifacts(
                                sections
                        );

        ProjectArtifact projectArtifact =
                artifactAssemblyService
                        .assembleProject(

                                generateProjectName(
                                        capability
                                ),

                                generateSummary(
                                        sections
                                ),

                                artifacts

                        );

        return AIResponse.builder()

                .responseId(
                        UUID.randomUUID().toString()
                )

                .title(
                        generateTitle(
                                capability,
                                sections
                        )
                )

                .summary(
                        generateSummary(
                                sections
                        )
                )

                .capability(capability)

                .domain(
                        ToolDomain.CODE
                )

                .rawResponse(rawResponse)

                .sections(sections)

                .metadata(

                        enrichMetadata(

                                metadata,

                                projectArtifact

                        )

                )

                .createdAt(
                        LocalDateTime.now()
                )

                .build();

    }

    // =========================
    // TITLE
    // =========================

    private String generateTitle(

            ToolCapability capability,

            List<ResponseSection> sections

    ) {

        boolean hasArchitecture =
                containsSection(
                        sections,
                        SectionType.ARCHITECTURE
                );

        boolean hasApi =
                containsSection(
                        sections,
                        SectionType.API
                );

        boolean hasDatabase =
                containsSection(
                        sections,
                        SectionType.DATABASE
                );

        if (
                hasArchitecture
                        && hasDatabase
        ) {

            return "Scalable Backend Architecture";

        }

        if (hasApi) {

            return "REST API Design";

        }

        return switch (capability) {

            case API_GENERATION ->
                    "API Generation Response";

            case DATABASE_DESIGN ->
                    "Database Design Response";

            case ARCHITECTURE_DESIGN ->
                    "Architecture Design Response";

            case CODE_GENERATION ->
                    "Code Generation Response";

            default ->
                    "Engineering Response";

        };

    }

    // =========================
    // SUMMARY
    // =========================

    private String generateSummary(
            List<ResponseSection> sections
    ) {

        int codeBlocks = 0;

        int apiBlocks = 0;

        int architectureBlocks = 0;

        int deploymentBlocks = 0;

        for (ResponseSection section : sections) {

            if (
                    section.getSectionType()
                            == SectionType.CODE
            ) {

                codeBlocks++;

            }

            if (
                    section.getSectionType()
                            == SectionType.API
            ) {

                apiBlocks++;

            }

            if (
                    section.getSectionType()
                            == SectionType.ARCHITECTURE
            ) {

                architectureBlocks++;

            }

            if (
                    section.getSectionType()
                            == SectionType.DEPLOYMENT
            ) {

                deploymentBlocks++;

            }

        }

        return """

                Generated engineering response containing:

                - %d code sections
                - %d API sections
                - %d architecture sections
                - %d deployment sections

                """.formatted(

                codeBlocks,

                apiBlocks,

                architectureBlocks,

                deploymentBlocks

        );

    }

    // =========================
    // METADATA
    // =========================

    private Map<String, Object> buildMetadata(
            List<ResponseSection> sections
    ) {

        Map<String, Object> metadata =
                new HashMap<>();

        metadata.put(

                "containsCode",

                containsSection(
                        sections,
                        SectionType.CODE
                )

        );

        metadata.put(

                "containsApi",

                containsSection(
                        sections,
                        SectionType.API
                )

        );

        metadata.put(

                "containsDatabase",

                containsSection(
                        sections,
                        SectionType.DATABASE
                )

        );

        metadata.put(

                "containsArchitecture",

                containsSection(
                        sections,
                        SectionType.ARCHITECTURE
                )

        );

        metadata.put(

                "containsDeployment",

                containsSection(
                        sections,
                        SectionType.DEPLOYMENT
                )

        );

        metadata.put(

                "exportable",

                containsSection(
                        sections,
                        SectionType.CODE
                )

        );

        metadata.put(

                "sectionCount",

                sections.size()

        );

        return metadata;

    }

    // =========================
    // HELPERS
    // =========================

    private boolean containsSection(

            List<ResponseSection> sections,

            SectionType sectionType

    ) {

        return sections.stream()

                .anyMatch(

                        section ->

                                section.getSectionType()
                                        == sectionType

                );

    }

    private Map<String, Object> enrichMetadata(

            Map<String, Object> metadata,

            ProjectArtifact projectArtifact

    ) {

        metadata.put(
                "projectArtifact",
                projectArtifact
        );

        metadata.put(
                "artifactCount",
                projectArtifact.getArtifacts().size()
        );

        metadata.put(
                "projectType",
                projectArtifact.getProjectType()
        );

        return metadata;

    }

    private String generateProjectName(
            ToolCapability capability
    ) {

        return switch (capability) {

            case API_GENERATION ->
                    "Generated API Project";

            case ARCHITECTURE_DESIGN ->
                    "Scalable Architecture Project";

            case DATABASE_DESIGN ->
                    "Database Design Project";

            case CODE_GENERATION ->
                    "Generated Backend Project";

            default ->
                    "Nexus Generated Project";

        };

    }

}