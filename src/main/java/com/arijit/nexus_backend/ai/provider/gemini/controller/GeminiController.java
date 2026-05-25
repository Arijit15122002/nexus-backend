package com.arijit.nexus_backend.ai.provider.gemini.controller;


import com.arijit.nexus_backend.ai.orchestrator.ChatOrchestratorService;
import com.arijit.nexus_backend.ai.provider.gemini.dto.ChatRequest;
import com.arijit.nexus_backend.user.entity.User;
import com.arijit.nexus_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/gemini")
@RequiredArgsConstructor
public class GeminiController {

    private final ChatOrchestratorService
            chatOrchestratorService;

    private final UserRepository userRepository;

    @PostMapping(
            value = "/chat",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public Flux<String> chat(
            @RequestBody ChatRequest request,
            Authentication authentication
    ) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return chatOrchestratorService.chat(
                request.getMessage(),
                request.getConversationId(),
                user
        );

    }
}