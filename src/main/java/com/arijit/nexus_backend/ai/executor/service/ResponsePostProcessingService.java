package com.arijit.nexus_backend.ai.executor.service;

import org.springframework.stereotype.Service;

@Service
public class ResponsePostProcessingService {

    public String processResponse(
            String response
    ) {

        if (response == null) {
            return "";
        }

        return response
                .replace("```java", "\n```java")
                .replace("```", "```")
                .trim();

    }

}