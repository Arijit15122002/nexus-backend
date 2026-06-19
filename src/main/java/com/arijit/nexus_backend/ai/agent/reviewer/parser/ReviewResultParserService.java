package com.arijit.nexus_backend.ai.agent.reviewer.parser;

import com.arijit.nexus_backend.ai.agent.reviewer.dto.ReviewIssue;
import com.arijit.nexus_backend.ai.agent.reviewer.dto.ReviewResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewResultParserService {

    private final ObjectMapper objectMapper;

    public ReviewResult parse(
            String response
    ) {

        try {

            String cleanedResponse =
                    response
                            .replace("```json", "")
                            .replace("```", "")
                            .trim();

            JsonNode root =
                    objectMapper.readTree(
                            cleanedResponse
                    );

            Integer score =
                    root.path("score")
                            .asInt();

            List<ReviewIssue> issues =
                    extractIssues(
                            root.path("issues")
                    );

            return ReviewResult.builder()

                    .score(score)

                    .issues(issues)

                    .build();

        }

        catch (Exception e) {

            throw new RuntimeException(
                    "Failed to parse review result",
                    e
            );

        }

    }

    private List<ReviewIssue> extractIssues(
            JsonNode issuesNode
    ) {

        List<ReviewIssue> issues =
                new ArrayList<>();

        if (
                issuesNode == null
                        || !issuesNode.isArray()
        ) {

            return issues;

        }

        issuesNode.forEach(

                node ->

                        issues.add(

                                ReviewIssue.builder()

                                        .severity(
                                                node.path("severity")
                                                        .asText()
                                        )

                                        .category(
                                                node.path("category")
                                                        .asText()
                                        )

                                        .file(
                                                node.path("file")
                                                        .asText()
                                        )

                                        .issue(
                                                node.path("issue")
                                                        .asText()
                                        )

                                        .recommendation(
                                                node.path("recommendation")
                                                        .asText()
                                        )

                                        .build()

                        )

        );

        return issues;

    }

}