package com.arijit.nexus_backend.ai.provider.openrouter.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenRouterService {

//    @Value("${openrouter.api.key}")
    private String apiKey = "blaablaaa";

//    @Value("${openrouter.model}")
    private String model = "blaablaa";

    private final ObjectMapper objectMapper;

    private final WebClient webClient =
            WebClient.builder()
                    .baseUrl("https://openrouter.ai/api/v1")
                    .build();

    public String generateResponse(
            String prompt
    ) {

        try {

            if (
                    prompt == null
                            || prompt.isBlank()
            ) {

                throw new RuntimeException(
                        "Prompt cannot be empty"
                );

            }

            Map<String, Object> requestBody =
                    Map.of(

                            "model",
                            model,

                            "messages",
                            List.of(
                                    Map.of(
                                            "role",
                                            "user",

                                            "content",
                                            prompt
                                    )
                            ),

                            "temperature",
                            0.2

                    );

            String response =
                    webClient.post()

                            .uri(
                                    "/chat/completions"
                            )

                            .header(
                                    HttpHeaders.AUTHORIZATION,
                                    "Bearer " + apiKey
                            )

                            .header(
                                    "HTTP-Referer",
                                    "http://localhost:8080"
                            )

                            .header(
                                    "X-Title",
                                    "Nexus AI"
                            )

                            .contentType(
                                    MediaType.APPLICATION_JSON
                            )

                            .bodyValue(
                                    requestBody
                            )

                            .retrieve()

                            .bodyToMono(
                                    String.class
                            )

                            .block();

            if (
                    response == null
                            || response.isBlank()
            ) {

                throw new RuntimeException(
                        "Empty response from OpenRouter"
                );

            }

            JsonNode root =
                    objectMapper.readTree(
                            response
                    );

            JsonNode contentNode =
                    root.path("choices")
                            .path(0)
                            .path("message")
                            .path("content");

            if (
                    contentNode.isMissingNode()
                            || contentNode.asText().isBlank()
            ) {

                throw new RuntimeException(
                        "OpenRouter returned empty content"
                );

            }

            return contentNode.asText();

        }

        catch (Exception e) {

            throw new RuntimeException(
                    "OpenRouter generation failed: "
                            + e.getMessage(),
                    e
            );

        }

    }

}