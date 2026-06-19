package com.arijit.nexus_backend.ai.provider.deepseek.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DeepSeekService {

//    @Value("${deepseek.api.key}")
    private String apiKey = "blaablaa";

    private final ObjectMapper objectMapper;

    private final WebClient webClient =
            WebClient.builder()
                    .baseUrl("https://api.deepseek.com")
                    .build();

    public String generateResponse(
            String prompt
    ) {

        try {

            Map<String,Object> requestBody =
                    Map.of(

                            "model",
                            "deepseek-chat",

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

                            .uri("/chat/completions")

                            .header(
                                    "Authorization",
                                    "Bearer " + apiKey
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

            JsonNode root =
                    objectMapper.readTree(
                            response
                    );

            return root.path("choices")
                    .path(0)
                    .path("message")
                    .path("content")
                    .asText();

        }

        catch (Exception e) {

            throw new RuntimeException(
                    "DeepSeek generation failed",
                    e
            );

        }

    }

}