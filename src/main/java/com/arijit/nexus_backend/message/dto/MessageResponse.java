package com.arijit.nexus_backend.message.dto;


import com.arijit.nexus_backend.message.entity.MessageRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MessageResponse {

    private Long id;
    private MessageRole role;
    private String content;
    private LocalDateTime createdAt;

}