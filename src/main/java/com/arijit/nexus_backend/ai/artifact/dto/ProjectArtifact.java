package com.arijit.nexus_backend.ai.artifact.dto;

import com.arijit.nexus_backend.ai.artifact.structure.ProjectStructureNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectArtifact {

    private String projectId;

    private String projectName;

    private String description;

    private String projectType;

    private List<CodeArtifact> artifacts;

    private Map<String, Object> metadata;

    private LocalDateTime createdAt;

    private ProjectStructureNode projectStructure;

}