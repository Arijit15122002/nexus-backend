package com.arijit.nexus_backend.ai.agent.technology.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class TechnologyReviewResult {

    private Integer score;

//    private Map<String,String> updatedVersions;

    private List<TechnologyIssue> issues;

}