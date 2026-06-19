package com.arijit.nexus_backend.ai.agent.developer.dto;

import com.arijit.nexus_backend.ai.agent.architect.dto.ArchitecturePlan;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeveloperRequest {

    private String userRequest;

    private ArchitecturePlan architecturePlan;

}