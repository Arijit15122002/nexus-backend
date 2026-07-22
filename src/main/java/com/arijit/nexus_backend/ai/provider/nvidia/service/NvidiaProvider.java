package com.arijit.nexus_backend.ai.provider.nvidia.service;

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
public class NvidiaProvider implements AIProvider {

    @Value("${nvidia.api.key}")
    private String apiKey;

    @Value("${nvidia.base.url}")
    private String baseUrl;

    private final ObjectMapper objectMapper;

    private WebClient webClient;

    @Override
    public AIProviderType getProvider() {
        return AIProviderType.NVIDIA;
    }

    @Override
    public AIResponse generate(AIRequest request) {

        try {

            if (webClient == null) {

                webClient = WebClient.builder()
                        .baseUrl(baseUrl)
                        .build();

            }

            Map<String, Object> requestBody = Map.of(

                    "model", request.getModel(),

                    "temperature", request.getTemperature(),

                    "top_p", 1,

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

            String response =

                    webClient.post()

                            .uri("/chat/completions")

                            .header(
                                    "Authorization",
                                    "Bearer " + apiKey
                            )

                            .header(
                                    "Accept",
                                    "application/json"
                            )

                            .contentType(MediaType.APPLICATION_JSON)

                            .bodyValue(requestBody)

                            .retrieve()

                            .onStatus(
                                    status -> status.isError(),
                                    responseBody -> responseBody.bodyToMono(String.class)
                                            .map(body -> new RuntimeException(
                                                    "NVIDIA Error: " + body
                                            ))
                            )

                            .bodyToMono(String.class)

                            .doOnNext(System.out::println)

                            .block();

            if (

                    response == null ||

                            response.isBlank()

            ) {

                throw new RuntimeException(
                        "Empty response from NVIDIA"
                );

            }

            JsonNode root = objectMapper.readTree(response);

            JsonNode contentNode =

                    root.path("choices")
                            .path(0)
                            .path("message")
                            .path("content");

            if (

                    contentNode.isMissingNode()

                            ||

                            contentNode.asText().isBlank()

            ) {

                throw new RuntimeException(
                        "No content returned from NVIDIA"
                );

            }

            return AIResponse.builder()

                    .provider("NVIDIA")

                    .model(request.getModel())

                    .content(contentNode.asText())

                    .build();

        }

        catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException(
                    "NVIDIA generation failed",
                    e
            );

        }

    }

}