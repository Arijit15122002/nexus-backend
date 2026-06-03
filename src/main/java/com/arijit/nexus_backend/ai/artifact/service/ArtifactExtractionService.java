package com.arijit.nexus_backend.ai.artifact.service;

import com.arijit.nexus_backend.ai.artifact.dto.CodeArtifact;
import com.arijit.nexus_backend.ai.artifact.entity.ArtifactType;
import com.arijit.nexus_backend.ai.response.dto.ResponseSection;
import com.arijit.nexus_backend.ai.response.entity.SectionType;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArtifactExtractionService {

    private final ArtifactNamingService
            artifactNamingService;

    private final ArtifactPathResolverService
            artifactPathResolverService;

    public List<CodeArtifact> extractArtifacts(
            List<ResponseSection> sections
    ) {

        List<CodeArtifact> artifacts =
                new ArrayList<>();

        for (ResponseSection section : sections) {

            if (
                    section.getSectionType()
                            != SectionType.CODE
            ) {

                continue;

            }

            artifacts.add(

                    CodeArtifact.builder()

                            .fileName(

                                    artifactNamingService
                                            .generateFileName(

                                                    section.getLanguage(),

                                                    section.getContent()

                                            )

                            )

                            .filePath(

                                    artifactPathResolverService
                                            .resolvePath(

                                                    artifactNamingService
                                                            .generateFileName(

                                                                    section.getLanguage(),

                                                                    section.getContent()

                                                            ),

                                                    determineArtifactType(section)

                                            )

                            )

                            .language(
                                    section.getLanguage()
                            )

                            .content(
                                    section.getContent()
                            )

                            .artifactType(
                                    determineArtifactType(section)
                            )

                            .exportable(true)

                            .build()

            );

        }

        return artifacts;

    }

    // =========================
    // ARTIFACT TYPE
    // =========================

    private ArtifactType determineArtifactType(
            ResponseSection section
    ) {

        String language =
                section.getLanguage();

        if (language == null) {

            return ArtifactType.UNKNOWN;

        }

        return switch (language) {

            case "java" ->
                    ArtifactType.BACKEND_SOURCE;

            case "javascript",
                 "typescript" ->
                    ArtifactType.FRONTEND_SOURCE;

            case "sql" ->
                    ArtifactType.DATABASE_SCHEMA;

            case "dockerfile",
                 "yaml" ->
                    ArtifactType.DEPLOYMENT_CONFIG;

            default ->
                    ArtifactType.UNKNOWN;

        };

    }

}