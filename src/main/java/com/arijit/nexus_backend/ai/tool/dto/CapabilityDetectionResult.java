package com.arijit.nexus_backend.ai.tool.dto;

import com.arijit.nexus_backend.ai.tool.entity.ToolCapability;
import com.arijit.nexus_backend.ai.tool.entity.ToolDomain;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CapabilityDetectionResult {

    private ToolCapability capability;

    private ToolDomain domain;

}