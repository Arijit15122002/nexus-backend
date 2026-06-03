package com.arijit.nexus_backend.ai.embedding.service;

import com.pgvector.PGvector;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmbeddingService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    // Switch from gemini-embedding-001 (shared quota) to text-embedding-004
    // text-embedding-004 has a SEPARATE quota of 1500 RPM on the free tier —
    // meaning it will never compete with your chat/classification Gemini calls.
    // Output dimension is 768 floats (same as gemini-embedding-001).

    private static final String EMBEDDING_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-embedding-001:embedContent?key=";

    public PGvector generateEmbedding(String text) {

        String url = EMBEDDING_URL + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "model", "models/text-embedding-004",   // ← added
                "content", Map.of("parts", List.of(Map.of("text", text))),
                "outputDimensionality", 3072            // ← added to match your vector(3072) column
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException("Gemini embedding API error: " + response.getStatusCode());
            }

            Map<?, ?> embedding = (Map<?, ?>) response.getBody().get("embedding");

            if (embedding == null || !embedding.containsKey("values")) {
                throw new RuntimeException("Gemini response missing embedding values");
            }

            List<?> rawValues = (List<?>) embedding.get("values");
            float[] vector = new float[rawValues.size()];

            for (int i = 0; i < rawValues.size(); i++) {
                vector[i] = ((Number) rawValues.get(i)).floatValue();
            }

            return new PGvector(vector);

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate embedding: " + e.getMessage(), e);
        }
    }
}