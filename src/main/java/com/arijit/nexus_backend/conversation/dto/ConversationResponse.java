package com.arijit.nexus_backend.conversation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ConversationResponse {

    private Long id;
    private String title;
    private LocalDateTime createdAt;

}
