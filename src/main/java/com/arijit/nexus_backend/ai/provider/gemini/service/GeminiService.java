package com.arijit.nexus_backend.ai.provider.gemini.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final ObjectMapper objectMapper;

    private final WebClient webClient =
            WebClient.builder()
                    .baseUrl(
                            "https://generativelanguage.googleapis.com"
                    )
                    .build();

    // =========================
    // STABLE MODEL
    // =========================

    private static final String MODEL_GENERATE =
            "/v1beta/models/gemini-2.0-flash:generateContent";

    // =========================
    // NORMAL RESPONSE
    // =========================

    public String generateResponse(
            String prompt
    ) {

        try {

            String cleanedPrompt =
                    prompt == null
                            ? ""
                            : prompt.trim();

            if (cleanedPrompt.isBlank()) {

                throw new RuntimeException(
                        "Prompt cannot be empty"
                );

            }

            Map<String, Object> requestBody =
                    Map.of(
                            "contents",
                            List.of(
                                    Map.of(
                                            "parts",
                                            List.of(
                                                    Map.of(
                                                            "text",
                                                            cleanedPrompt
                                                    )
                                            )
                                    )
                            )
                    );

            String response =
                    webClient.post()

                            .uri(uriBuilder ->
                                    uriBuilder
                                            .path(MODEL_GENERATE)
                                            .queryParam(
                                                    "key",
                                                    apiKey
                                            )
                                            .build()
                            )

                            .contentType(
                                    MediaType.APPLICATION_JSON
                            )

                            .bodyValue(requestBody)

                            .retrieve()

                            .bodyToMono(String.class)

                            .block();

            if (
                    response == null
                            || response.isBlank()
            ) {

                throw new RuntimeException(
                        "Empty response from Gemini"
                );

            }

            JsonNode root =
                    objectMapper.readTree(response);

            JsonNode textNode =
                    root.path("candidates")
                            .path(0)
                            .path("content")
                            .path("parts")
                            .path(0)
                            .path("text");

            if (
                    textNode.isMissingNode()
                            || textNode.asText().isBlank()
            ) {

                throw new RuntimeException(
                        "Gemini returned empty text"
                );

            }

            return textNode.asText();

        }

        catch (Exception e) {

            throw new RuntimeException(
                    "Gemini generation failed: "
                            + e.getMessage(),
                    e
            );

        }

    }

    // =========================
    // FAKE STREAMING
    // =========================

    public Flux<String> generateResponseStream(
            String prompt
    ) {

        return Flux.create(sink -> {

            try {

                String response =
                        generateResponse(prompt);

                sink.next(response);

                sink.complete();

            }

            catch (Exception e) {

                sink.error(e);

            }

        });

    }

}