package com.arijit.nexus_backend.ai.agent.refactorer.service;

import com.arijit.nexus_backend.ai.agent.reviewer.dto.ReviewResult;
import org.springframework.stereotype.Service;

@Service
public class RefactorerPromptBuilderService {

    public String buildPrompt(
            String userRequest,
            String generatedProject,
            ReviewResult reviewResult
    ) {

        return """

You are a Principal Refactoring Engineer.

The Developer Agent has already generated a project.

The Reviewer Agent has already identified problems.

Your responsibility is:

- fix issues
- improve security
- improve scalability
- improve maintainability
- improve code quality

Do NOT redesign architecture.

Preserve architecture.

Refactor implementation only.

==================================================
REVIEW FINDINGS
==================================================

%s

==================================================
GENERATED PROJECT
==================================================

%s

==================================================
USER REQUEST
==================================================

%s

==================================================
OUTPUT FORMAT
==================================================

Return the COMPLETE corrected project.

For every file use:

FILE: relative/path/FileName.ext

LANGUAGE: language

```language
complete source code

Return all modified files.

"""
                .formatted(
                        reviewResult,
                        generatedProject,
                        userRequest
                );

    }

}