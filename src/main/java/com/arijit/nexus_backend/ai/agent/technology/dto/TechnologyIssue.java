package com.arijit.nexus_backend.ai.agent.technology.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TechnologyIssue {

    private String technology;

    private String currentVersion;

    private String recommendedVersion;

    private String reason;

    private String severity;

    private String replacement;

}