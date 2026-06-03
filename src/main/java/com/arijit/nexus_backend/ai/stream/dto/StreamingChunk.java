package com.arijit.nexus_backend.ai.stream.dto;

import com.arijit.nexus_backend.ai.artifact.dto.CodeArtifact;
import com.arijit.nexus_backend.ai.artifact.structure.ProjectStructureNode;
import com.arijit.nexus_backend.ai.stream.entity.StreamingChunkType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// Only include non-null fields in JSON so TEXT chunks stay lightweight
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StreamingChunk {

    private StreamingChunkType type;

    private String content;

    private CodeArtifact artifact;

    private ProjectStructureNode projectStructure;

    private Map<String, Object> metadata;

    private boolean completed;
}