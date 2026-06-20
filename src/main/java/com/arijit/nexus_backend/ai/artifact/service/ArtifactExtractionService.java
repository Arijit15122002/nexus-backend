package com.arijit.nexus_backend.ai.artifact.service;

import com.arijit.nexus_backend.ai.artifact.dto.CodeArtifact;
import com.arijit.nexus_backend.ai.artifact.entity.ArtifactType;
import com.arijit.nexus_backend.ai.response.dto.ResponseSection;
import com.arijit.nexus_backend.ai.response.entity.SectionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArtifactExtractionService {

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

            String fullPath =
                    section.getTitle();

            if (
                    fullPath == null
                            || fullPath.isBlank()
            ) {

                continue;

            }

            String fileName;

            String filePath;

            if (fullPath.contains("/")) {

                fileName =
                        fullPath.substring(
                                fullPath.lastIndexOf("/") + 1
                        );

                filePath =
                        fullPath.substring(
                                0,
                                fullPath.lastIndexOf("/")
                        );

            }

            else {

                fileName =
                        fullPath;

                filePath =
                        "";

            }

            artifacts.add(

                    CodeArtifact.builder()

                            .fileName(fileName)

                            .filePath(filePath)

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
                 "typescript",
                 "jsx",
                 "tsx" ->
                    ArtifactType.FRONTEND_SOURCE;

            case "sql" ->
                    ArtifactType.DATABASE_SCHEMA;

            case "dockerfile",
                 "yaml",
                 "yml" ->
                    ArtifactType.DEPLOYMENT_CONFIG;

            default ->
                    ArtifactType.UNKNOWN;

        };

    }

}