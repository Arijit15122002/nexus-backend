package com.arijit.nexus_backend.ai.provider.groq.service;

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
public class GroqService {

    @Value("${groq.api.key}")
    private String apiKey;

    private final ObjectMapper objectMapper;

    private final WebClient webClient =
            WebClient.builder()
                    .baseUrl("https://api.groq.com/openai/v1")
                    .build();

    /**
     * Default ORKA Developer Agent.
     * Existing services continue using this method without any changes.
     */
    public String generateResponse(String prompt) {

        return generateResponse(

                """
                You are ORKA Developer Agent.

                Follow output format EXACTLY.

                Every file MUST begin with:

                FILE: relative/path/FileName.ext
                LANGUAGE: language

                No explanations.
                No notes.
                No markdown outside files.

                If FILE is missing,
                the response is invalid.
                """,

                prompt

        );

    }

    /**
     * Generic Groq invocation.
     * Allows every future Agent to define its own system prompt.
     */
    public String generateResponse(

            String systemPrompt,

            String userPrompt

    ) {

        try {

            Map<String, Object> requestBody = Map.of(

                    "model", "llama-3.3-70b-versatile",

                    "temperature", 0.1,

                    "max_tokens", 8192,

                    "messages",

                    List.of(

                            Map.of(

                                    "role", "system",

                                    "content", systemPrompt

                            ),

                            Map.of(

                                    "role", "user",

                                    "content", userPrompt

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

                            .contentType(MediaType.APPLICATION_JSON)

                            .bodyValue(requestBody)

                            .retrieve()

                            .bodyToMono(String.class)

                            .block();

            if (

                    response == null ||

                            response.isBlank()

            ) {

                throw new RuntimeException(
                        "Empty response from Groq"
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

                            ||

                            contentNode.asText().isBlank()

            ) {

                throw new RuntimeException(
                        "No content returned from Groq"
                );

            }

            return contentNode.asText();

        }

        catch (Exception e) {

            throw new RuntimeException(

                    "Groq generation failed: "

                            + e.getMessage(),

                    e

            );

        }

    }

    /**
     * Backward-compatible fake streaming.
     */
    public Flux<String> generateResponseStream(
            String prompt
    ) {

        return Flux.create(sink -> {

            try {

                String response =
                        generateResponse(prompt);

                String[] chunks =
                        response.split(" ");

                for (String chunk : chunks) {

                    sink.next(chunk + " ");

                    Thread.sleep(25);

                }

                sink.complete();

            }

            catch (Exception e) {

                sink.error(e);

            }

        });

    }

}