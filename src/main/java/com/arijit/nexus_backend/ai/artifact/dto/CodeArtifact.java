package com.arijit.nexus_backend.ai.artifact.dto;

import com.arijit.nexus_backend.ai.artifact.entity.ArtifactType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeArtifact {

    private String fileName;

    private String filePath;

    private String language;

    private String content;

    private ArtifactType artifactType;

    private boolean exportable;

    private boolean executable;

    private boolean entryPoint;

}