package com.arijit.nexus_backend.ai.response.dto;

import com.arijit.nexus_backend.ai.response.entity.RenderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIResponseMetadata {

    private RenderType renderType;

    private String difficulty;

    private String estimatedDuration;

    private String priority;

    private String category;

}