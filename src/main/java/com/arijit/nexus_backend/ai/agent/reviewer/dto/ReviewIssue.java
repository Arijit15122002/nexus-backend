package com.arijit.nexus_backend.ai.agent.reviewer.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewIssue {

    private String severity;

    private String category;

    private String file;

    private String issue;

    private String recommendation;

}