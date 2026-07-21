package com.arijit.nexus_backend.ai.provider.groq.service;

import com.arijit.nexus_backend.ai.provider.dto.AIRequest;
import com.arijit.nexus_backend.ai.provider.dto.AIResponse;
import com.arijit.nexus_backend.ai.provider.model.AIProviderType;
import com.arijit.nexus_backend.ai.provider.service.AIProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GroqProvider implements AIProvider {

    @Value("${groq.api.key}")
    private String apiKey;

    private final ObjectMapper objectMapper;

    private final WebClient webClient =
            WebClient.builder()
                    .baseUrl("https://api.groq.com/openai/v1")
                    .build();

    @Override
    public AIProviderType getProvider() {
        return AIProviderType.GROQ;
    }

    @Override
    public AIResponse generate(AIRequest request) {

        try {

            Map<String, Object> requestBody = Map.of(

                    "model", request.getModel(),

                    "temperature", request.getTemperature(),

                    "max_tokens", request.getMaxTokens(),

                    "stream", request.getStream(),

                    "messages",

                    List.of(

                            Map.of(
                                    "role", "system",
                                    "content", request.getSystemPrompt()
                            ),

                            Map.of(
                                    "role", "user",
                                    "content", request.getUserPrompt()
                            )

                    )

            );

            String response = webClient.post()

                    .uri("/chat/completions")

                    .header(
                            "Authorization",
                            "Bearer " + apiKey
                    )

                    .contentType(MediaType.APPLICATION_JSON)

                    .bodyValue(requestBody)

                    .retrieve()

                    .bodyToMono(String.class)

                    .block();

            if (response == null || response.isBlank()) {

                throw new RuntimeException("Empty response from Groq");

            }

            JsonNode root = objectMapper.readTree(response);

            String content = root.path("choices")
                    .path(0)
                    .path("message")
                    .path("content")
                    .asText();

            return AIResponse.builder()

                    .provider("GROQ")

                    .model(request.getModel())

                    .content(content)

                    .build();

        }

        catch (Exception e) {

            throw new RuntimeException(

                    "Groq Provider failed : "

                            + e.getMessage(),

                    e

            );

        }

    }

}