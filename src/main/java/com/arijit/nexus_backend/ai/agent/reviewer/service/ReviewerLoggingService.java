package com.arijit.nexus_backend.ai.agent.reviewer.service;

import com.arijit.nexus_backend.ai.agent.reviewer.dto.ReviewIssue;
import com.arijit.nexus_backend.ai.agent.reviewer.dto.ReviewResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ReviewerLoggingService {

    public void logReviewResult(
            ReviewResult result
    ) {

        log.info(
                "\n================ REVIEW RESULT ================\n"
        );

        log.info(
                "SCORE = {}",
                result.getScore()
        );

        for (
                ReviewIssue issue :
                result.getIssues()
        ) {

            log.info(
                    """
                    --------------------------------
                    SEVERITY: {}
                    CATEGORY: {}
                    FILE: {}
                    ISSUE: {}
                    RECOMMENDATION: {}
                    """,
                    issue.getSeverity(),
                    issue.getCategory(),
                    issue.getFile(),
                    issue.getIssue(),
                    issue.getRecommendation()
            );

        }

        log.info(
                "\n================================================\n"
        );

    }

}