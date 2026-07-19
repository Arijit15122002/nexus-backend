package com.arijit.nexus_backend.message.controller;

import com.arijit.nexus_backend.message.dto.MessageResponse;
import com.arijit.nexus_backend.message.service.MessageService;
import com.arijit.nexus_backend.user.entity.User;
import com.arijit.nexus_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final UserRepository userRepository;

    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<?> getMessages(

            @PathVariable Long conversationId,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,

            Authentication authentication

    ) {

        try {

            UserDetails principal =
                    (UserDetails) authentication.getPrincipal();

            String email = principal.getUsername();

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() ->
                            new RuntimeException("User not found"));

            Page<MessageResponse> messages =
                    messageService.getMessages(
                            conversationId,
                            user,
                            page,
                            size
                    );

            return ResponseEntity.ok(messages);

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());

        }
    }
}