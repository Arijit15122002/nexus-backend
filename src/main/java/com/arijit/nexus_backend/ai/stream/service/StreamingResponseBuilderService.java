package com.arijit.nexus_backend.ai.stream.service;

import com.arijit.nexus_backend.ai.artifact.dto.CodeArtifact;
import com.arijit.nexus_backend.ai.artifact.structure.ProjectStructureNode;
import com.arijit.nexus_backend.ai.response.dto.ResponseSection;
import com.arijit.nexus_backend.ai.stream.dto.StreamingChunk;
import com.arijit.nexus_backend.ai.stream.entity.StreamingChunkType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Service
public class StreamingResponseBuilderService {

    public Flux<StreamingChunk> build(

            String rawResponse,

            List<ResponseSection> sections,

            List<CodeArtifact> artifacts,

            ProjectStructureNode structure

    ) {

        List<StreamingChunk> chunks =
                new ArrayList<>();

        // =========================
        // TEXT RESPONSE
        // =========================

        chunks.add(

                StreamingChunk.builder()
                        .type(StreamingChunkType.TEXT)
                        .content(rawResponse)
                        .completed(false)
                        .build()

        );

        // =========================
        // SECTIONS
        // =========================

        for (ResponseSection section : sections) {

            chunks.add(

                    StreamingChunk.builder()
                            .type(StreamingChunkType.SECTION)
                            .metadata(
                                    java.util.Map.of(
                                            "title",
                                            section.getTitle(),

                                            "type",
                                            section.getSectionType()
                                    )
                            )
                            .content(section.getContent())
                            .completed(false)
                            .build()

            );

        }

        // =========================
        // ARTIFACTS
        // =========================

        for (CodeArtifact artifact : artifacts) {

            chunks.add(

                    StreamingChunk.builder()
                            .type(StreamingChunkType.ARTIFACT)
                            .artifact(artifact)
                            .completed(false)
                            .build()

            );

        }

        // =========================
        // PROJECT STRUCTURE
        // =========================

        chunks.add(

                StreamingChunk.builder()
                        .type(
                                StreamingChunkType.PROJECT_STRUCTURE
                        )
                        .projectStructure(structure)
                        .completed(false)
                        .build()

        );

        // =========================
        // COMPLETE
        // =========================

        chunks.add(

                StreamingChunk.builder()
                        .type(StreamingChunkType.COMPLETE)
                        .completed(true)
                        .build()

        );

        return Flux.fromIterable(chunks);

    }

}