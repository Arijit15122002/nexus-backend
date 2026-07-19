package com.arijit.nexus_backend.conversation.controller;

import com.arijit.nexus_backend.conversation.dto.ConversationResponse;
import com.arijit.nexus_backend.conversation.service.ConversationService;
import com.arijit.nexus_backend.user.entity.User;
import com.arijit.nexus_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getConversations(

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication

    ) {

        try {

            UserDetails principal =
                    (UserDetails) authentication.getPrincipal();

            String email = principal.getUsername();

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() ->
                            new RuntimeException("User not found"));

            Page<ConversationResponse> conversations =
                    conversationService.getConversations(
                            user.getId(),
                            page,
                            size
                    );

            return ResponseEntity.ok(conversations);

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());

        }
    }
}