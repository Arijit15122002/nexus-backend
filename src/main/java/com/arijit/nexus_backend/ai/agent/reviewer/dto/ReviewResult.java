package com.arijit.nexus_backend.ai.agent.reviewer.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ReviewResult {

    private Integer score;

    private List<ReviewIssue> issues;

}