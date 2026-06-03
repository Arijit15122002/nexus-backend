package com.arijit.nexus_backend.ai.provider.gemini.controller;

import com.arijit.nexus_backend.ai.orchestrator.ChatOrchestratorService;
import com.arijit.nexus_backend.ai.provider.gemini.dto.ChatRequest;
import com.arijit.nexus_backend.ai.stream.dto.StreamingChunk;
import com.arijit.nexus_backend.user.entity.User;
import com.arijit.nexus_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/gemini")
@RequiredArgsConstructor
public class GeminiController {

    private final ChatOrchestratorService
            chatOrchestratorService;

    private final UserRepository
            userRepository;

    @PostMapping(
            value = "/chat",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public Flux<ServerSentEvent<StreamingChunk>> chat(

            @RequestBody ChatRequest request,

            Authentication authentication

    ) {

        UserDetails principal =
                (UserDetails) authentication.getPrincipal();

        String email =
                principal.getUsername();

        User user =
                userRepository.findByEmail(email)

                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found: " + email
                                )
                        );

        return chatOrchestratorService

                .chat(
                        request.getMessage(),
                        request.getConversationId(),
                        user
                )

                .map(chunk ->

                        ServerSentEvent
                                .<StreamingChunk>builder()
                                .data(chunk)
                                .build()

                );

    }

}