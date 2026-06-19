package com.arijit.nexus_backend.ai.agent.reviewer.service;

import org.springframework.stereotype.Service;

@Service
public class ReviewerPromptBuilderService {

    public String buildPrompt(

            String userRequest,

            String generatedProject

    ) {

        return """

You are a Principal Software Reviewer.

Review the generated implementation.

Focus on:

- Architecture
- Security
- Performance
- Scalability
- Maintainability
- Code Quality
- Deprecated APIs
- Missing Components
- Production Readiness

Return ONLY JSON.

Format:

{
  "score":0,
  "issues":[
    {
      "severity":"",
      "category":"",
      "file":"",
      "issue":"",
      "recommendation":""
    }
  ]
}

USER REQUEST:

%s

GENERATED PROJECT:

%s

"""
                .formatted(
                        userRequest,
                        generatedProject
                );

    }

}