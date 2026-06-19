package com.arijit.nexus_backend.ai.agent.refactorer.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RefactorRequest {

    private String userRequest;

    private String generatedProject;

    private List<RefactorIssue> issues;

}