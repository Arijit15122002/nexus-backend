package com.arijit.nexus_backend.ai.agent.architect.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ArchitecturePlan {

    private String projectType;

    private String architectureStyle;

    private List<String> technologies;

    private List<String> modules;

    private List<String> files;

    private String reasoning;

}