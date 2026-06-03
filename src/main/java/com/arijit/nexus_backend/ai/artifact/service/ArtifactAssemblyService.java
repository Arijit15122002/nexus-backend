package com.arijit.nexus_backend.ai.artifact.service;

import com.arijit.nexus_backend.ai.artifact.dto.CodeArtifact;
import com.arijit.nexus_backend.ai.artifact.dto.ProjectArtifact;
import com.arijit.nexus_backend.ai.artifact.structure.ProjectStructureBuilderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArtifactAssemblyService {

    private final ProjectStructureBuilderService
            projectStructureBuilderService;

    public ProjectArtifact assembleProject(

            String projectName,

            String description,

            List<CodeArtifact> artifacts

    ) {

        return ProjectArtifact.builder()

                .projectId(
                        UUID.randomUUID().toString()
                )

                .projectName(
                        projectName
                )

                .description(
                        description
                )

                .projectType(
                        determineProjectType(
                                artifacts
                        )
                )

                .artifacts(
                        artifacts
                )

                .metadata(
                        buildMetadata(
                                artifacts
                        )
                )

                .projectStructure(

                        projectStructureBuilderService
                                .buildStructure(
                                        artifacts
                                )

                )

                .createdAt(
                        LocalDateTime.now()
                )

                .build();

    }

    // =========================
    // PROJECT TYPE
    // =========================

    private String determineProjectType(
            List<CodeArtifact> artifacts
    ) {

        boolean hasBackend =
                artifacts.stream()

                        .anyMatch(

                                artifact ->

                                        artifact.getLanguage()
                                                != null

                                                && artifact.getLanguage()
                                                .equals("java")

                        );

        boolean hasFrontend =
                artifacts.stream()

                        .anyMatch(

                                artifact ->

                                        artifact.getLanguage()
                                                != null

                                                && (

                                                artifact.getLanguage()
                                                        .equals("javascript")

                                                        ||

                                                        artifact.getLanguage()
                                                                .equals("typescript")

                                        )

                        );

        if (
                hasBackend
                        && hasFrontend
        ) {

            return "FULL_STACK";

        }

        if (hasBackend) {

            return "BACKEND";

        }

        if (hasFrontend) {

            return "FRONTEND";

        }

        return "GENERAL";

    }

    // =========================
    // METADATA
    // =========================

    private HashMap<String, Object> buildMetadata(
            List<CodeArtifact> artifacts
    ) {

        HashMap<String, Object> metadata =
                new HashMap<>();

        metadata.put(
                "artifactCount",
                artifacts.size()
        );

        metadata.put(
                "exportable",
                true
        );

        metadata.put(
                "generatedBy",
                "Nexus AI"
        );

        metadata.put(
                "generationTimestamp",
                LocalDateTime.now().toString()
        );

        return metadata;

    }

}