package com.arijit.nexus_backend.ai.provider.openrouter.service;

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
public class OpenRouterService {

    @Value("${openrouter.api.key}")
    private String apiKey;

    private final ObjectMapper objectMapper;

    private final WebClient webClient =
            WebClient.builder()
                    .baseUrl("https://openrouter.ai/api/v1")
                    .build();

    public String generateResponse(String prompt) {

        try {

            Map<String, Object> requestBody = Map.of(
                    "model", "meta-llama/llama-3.3-8b-instruct:free",
                    "messages", List.of(
                            Map.of(
                                    "role", "user",
                                    "content", prompt
                            )
                    )
            );

            String response =
                    webClient.post()
                            .uri("/chat/completions")
                            .header("Authorization", "Bearer " + apiKey)
                            .header("HTTP-Referer", "http://localhost:8080")
                            .header("X-Title", "nexus-ai")
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(requestBody)
                            .retrieve()
                            .bodyToMono(String.class)
                            .block();

            if (response == null || response.isBlank()) {

                throw new RuntimeException(
                        "Empty response from OpenRouter"
                );

            }

            JsonNode root =
                    objectMapper.readTree(response);

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
                        "No AI response content found"
                );

            }

            return contentNode.asText();

        }

        catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException(
                    "OpenRouter generation failed: "
                            + e.getMessage(),
                    e
            );

        }

    }

    public Flux<String> generateResponseStream(String prompt) {

        return Flux.create(sink -> {

            try {

                String response =
                        generateResponse(prompt);

                String[] chunks =
                        response.split(" ");

                for (String chunk : chunks) {

                    sink.next(chunk + " ");

                    Thread.sleep(30);

                }

                sink.complete();

            }

            catch (Exception e) {

                sink.error(e);

            }

        });

    }

}