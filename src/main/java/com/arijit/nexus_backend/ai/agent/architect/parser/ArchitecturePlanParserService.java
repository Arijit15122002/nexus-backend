package com.arijit.nexus_backend.ai.agent.architect.parser;

import com.arijit.nexus_backend.ai.agent.architect.dto.ArchitecturePlan;
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
public class ArchitecturePlanParserService {

    private final ObjectMapper objectMapper;

    public ArchitecturePlan parse(
            String response
    ) {

        try {

            String cleanedResponse =
                    extractJson(response);

            JsonNode root =
                    objectMapper.readTree(
                            cleanedResponse
                    );

            return ArchitecturePlan.builder()

                    .projectType(
                            root.path("projectType")
                                    .asText()
                    )

                    .architectureStyle(
                            root.path("architectureStyle")
                                    .asText()
                    )

                    .technologies(
                            extractArray(
                                    root,
                                    "technologies"
                            )
                    )

//                    .versions(
//                            extractMap(
//                                    root,
//                                    "versions"
//                            )
//                    )

                    .patterns(
                            extractArray(
                                    root,
                                    "patterns"
                            )
                    )

                    .databaseStrategy(
                            root.path(
                                    "databaseStrategy"
                            ).asText()
                    )

                    .deploymentStrategy(
                            root.path(
                                    "deploymentStrategy"
                            ).asText()
                    )

                    .securityStrategy(
                            root.path(
                                    "securityStrategy"
                            ).asText()
                    )

                    .modules(
                            extractArray(
                                    root,
                                    "modules"
                            )
                    )

                    .files(
                            extractArray(
                                    root,
                                    "files"
                            )
                    )

                    .reasoning(
                            root.path("reasoning").asText()
                    )

                    .build();

        }

        catch (Exception e) {

            throw new RuntimeException(
                    "Failed to parse Architecture Plan",
                    e
            );

        }

    }

    private List<String> extractArray(
            JsonNode root,
            String field
    ) {

        List<String> result =
                new ArrayList<>();

        root.path(field)
                .forEach(
                        node ->
                                result.add(
                                        node.asText()
                                )
                );

        return result;

    }

    private Map<String, String> extractMap(
            JsonNode root,
            String field
    ) {

        Map<String, String> result =
                new HashMap<>();

        JsonNode node =
                root.path(field);

        node.fields()
                .forEachRemaining(

                        entry ->

                                result.put(
                                        entry.getKey(),
                                        entry.getValue().asText()
                                )

                );

        return result;

    }

    private String extractJson(
            String response
    ) {

        int start =
                response.indexOf("{");

        int end =
                response.lastIndexOf("}");

        if (
                start == -1
                        || end == -1
        ) {

            throw new RuntimeException(
                    "No JSON found in Architect response"
            );

        }

        return response.substring(
                start,
                end + 1
        );

    }

}