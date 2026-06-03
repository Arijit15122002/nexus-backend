package com.arijit.nexus_backend.ai.artifact.structure;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectStructureNode {

    private String name;

    private String path;

    private boolean directory;

    @Builder.Default
    private List<ProjectStructureNode> children =
            new ArrayList<>();

}