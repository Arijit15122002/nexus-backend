package com.arijit.nexus_backend.ai.provider.gemini.dto;

import lombok.Data;

@Data
public class ChatRequest {

    private String message;
    private Long conversationId;

}
