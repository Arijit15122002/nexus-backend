package com.arijit.nexus_backend.ai.executor.dto;

import com.arijit.nexus_backend.ai.executor.entity.ExecutionMode;
import com.arijit.nexus_backend.ai.tool.entity.ToolCapability;
import com.arijit.nexus_backend.ai.tool.entity.ToolDomain;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExecutionContext {

    private String userMessage;

    private String memoryContext;

    private String finalPrompt;

    private ToolCapability capability;

    private ToolDomain domain;

    private ExecutionMode executionMode;

}