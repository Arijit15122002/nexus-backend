package com.arijit.nexus_backend.ai.response.dto;

import com.arijit.nexus_backend.ai.tool.entity.ToolCapability;
import com.arijit.nexus_backend.ai.tool.entity.ToolDomain;
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
public class AIResponse {

    private String responseId;

    private String title;

    private String summary;

    private ToolCapability capability;

    private ToolDomain domain;

    private String rawResponse;

    private List<ResponseSection> sections;

    private Map<String, Object> metadata;

    private LocalDateTime createdAt;

}