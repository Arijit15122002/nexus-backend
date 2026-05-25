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
                    .baseUrl("https://generativelanguage.googleapis.com")
                    .build();

    // =========================
    // NORMAL RESPONSE
    // =========================

    public String generateResponse(String prompt) {

        try {

            String cleanedPrompt =
                    prompt == null ? "" : prompt.trim();

            if (cleanedPrompt.isBlank()) {
                throw new RuntimeException(
                        "Prompt cannot be empty"
                );
            }

            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of(
                                    "parts", List.of(
                                            Map.of(
                                                    "text",
                                                    cleanedPrompt
                                            )
                                    )
                            )
                    )
            );

            String response =
                    webClient
                            .post()
                            .uri(uriBuilder ->
                                    uriBuilder
                                            .path(
                                                    "/v1beta/models/gemini-2.5-flash:generateContent"
                                            )
                                            .queryParam(
                                                    "key",
                                                    apiKey
                                            )
                                            .build()
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(requestBody)
                            .retrieve()
                            .bodyToMono(String.class)
                            .block();

            if (response == null || response.isBlank()) {

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

            if (textNode.isMissingNode()
                    || textNode.asText().isBlank()) {

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
    // REAL STREAMING RESPONSE
    // =========================

    public Flux<String> generateResponseStream(String prompt) {

        try {

            String cleanedPrompt =
                    prompt == null ? "" : prompt.trim();

            if (cleanedPrompt.isBlank()) {

                return Flux.error(
                        new RuntimeException(
                                "Prompt cannot be empty"
                        )
                );

            }

            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of(
                                    "parts", List.of(
                                            Map.of(
                                                    "text",
                                                    cleanedPrompt
                                            )
                                    )
                            )
                    )
            );

            return webClient
                    .post()

                    .uri(uriBuilder ->
                            uriBuilder
                                    .path(
                                            "/v1beta/models/gemini-2.5-flash:streamGenerateContent"
                                    )
                                    .queryParam(
                                            "alt",
                                            "sse"
                                    )
                                    .queryParam(
                                            "key",
                                            apiKey
                                    )
                                    .build()
                    )

                    .contentType(MediaType.APPLICATION_JSON)

                    .accept(MediaType.TEXT_EVENT_STREAM)

                    .bodyValue(requestBody)

                    .retrieve()

                    .bodyToFlux(String.class)

                    .flatMap(chunk -> {

                        try {

                            // =========================
                            // CLEAN SSE DATA
                            // =========================

                            String cleanedChunk =
                                    chunk
                                            .replace("data: ", "")
                                            .trim();

                            if (cleanedChunk.isBlank()) {
                                return Flux.empty();
                            }

                            // =========================
                            // PARSE JSON
                            // =========================

                            JsonNode root =
                                    objectMapper.readTree(
                                            cleanedChunk
                                    );

                            JsonNode textNode =
                                    root.path("candidates")
                                            .path(0)
                                            .path("content")
                                            .path("parts")
                                            .path(0)
                                            .path("text");

                            if (textNode.isMissingNode()) {
                                return Flux.empty();
                            }

                            String text =
                                    textNode.asText();

                            if (text.isBlank()) {
                                return Flux.empty();
                            }

                            return Flux.just(text);

                        }

                        catch (Exception e) {

                            return Flux.empty();

                        }

                    });

        }

        catch (Exception e) {

            return Flux.error(
                    new RuntimeException(
                            "Gemini streaming failed: "
                                    + e.getMessage(),
                            e
                    )
            );

        }

    }

}