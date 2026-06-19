package com.arijit.nexus_backend.ai.agent.technology.parser;

import com.arijit.nexus_backend.ai.agent.technology.dto.TechnologyIssue;
import com.arijit.nexus_backend.ai.agent.technology.dto.TechnologyReviewResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TechnologyResultParserService {

    private final ObjectMapper objectMapper;

    public TechnologyReviewResult parse(
            String response
    ) {

        try {

            String cleanedResponse =
                    extractJson(
                            response
                    );

            JsonNode root =
                    objectMapper.readTree(
                            cleanedResponse
                    );

            Map<String, String> versions =
                    new HashMap<>();

            JsonNode versionNode =
                    root.path(
                            "updatedVersions"
                    );

            versionNode.fields()
                    .forEachRemaining(

                            entry ->

                                    versions.put(
                                            entry.getKey(),
                                            entry.getValue().asText()
                                    )

                    );

            List<TechnologyIssue> issues =
                    new ArrayList<>();

            root.path("issues")
                    .forEach(

                            issue ->

                                    issues.add(

                                            TechnologyIssue.builder()

                                                    .technology(
                                                            issue.path("technology")
                                                                    .asText()
                                                    )

                                                    .currentVersion(
                                                            issue.path("currentVersion")
                                                                    .asText()
                                                    )

                                                    .recommendedVersion(
                                                            issue.path("recommendedVersion")
                                                                    .asText()
                                                    )

                                                    .replacement(
                                                            issue.path("replacement")
                                                                    .asText()
                                                    )

                                                    .reason(
                                                            issue.path("reason")
                                                                    .asText()
                                                    )

                                                    .severity(
                                                            issue.path("severity")
                                                                    .asText()
                                                    )

                                                    .build()

                                    )

                    );

            return TechnologyReviewResult.builder()

                    .score(
                            root.path("score")
                                    .asInt()
                    )

//                    .updatedVersions(
//                            versions
//                    )

                    .issues(
                            issues
                    )

                    .build();

        }

        catch (Exception e) {

            throw new RuntimeException(
                    "Technology Review Parse Failed",
                    e
            );

        }

    }

    private String extractJson(
            String response
    ) {

        if (
                response == null
                        || response.isBlank()
        ) {

            throw new RuntimeException(
                    "Technology response is empty"
            );

        }

        String cleaned =
                response

                        .replace(
                                "```json",
                                ""
                        )

                        .replace(
                                "```",
                                ""
                        );

        int start =
                cleaned.indexOf("{");

        int end =
                cleaned.lastIndexOf("}");

        if (
                start == -1
                        || end == -1
        ) {

            throw new RuntimeException(
                    "No JSON found in Technology Response:\n"
                            + response
            );

        }

        cleaned =
                cleaned.substring(
                        start,
                        end + 1
                );

        cleaned =
                cleaned.replaceAll(
                        "//.*",
                        ""
                );

        return cleaned.trim();

    }

}