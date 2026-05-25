package com.arijit.nexus_backend.ai.classifier.dto;

import com.arijit.nexus_backend.message.entity.MemoryType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemoryClassificationResult {

    private MemoryType memoryType;
    private Integer importanceScore;

}
