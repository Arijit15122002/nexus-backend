package com.arijit.nexus_backend.auth.controller;

import com.arijit.nexus_backend.ai.provider.dto.AIRequest;
import com.arijit.nexus_backend.ai.provider.dto.AIResponse;
import com.arijit.nexus_backend.ai.provider.model.AIProviderType;
import com.arijit.nexus_backend.ai.provider.service.AIService;
import com.arijit.nexus_backend.auth.dto.AuthResponse;
import com.arijit.nexus_backend.auth.dto.LoginRequest;
import com.arijit.nexus_backend.auth.dto.RegisterRequest;
import com.arijit.nexus_backend.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AIService aiService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try{
            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try{
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
    }

    @GetMapping("/health-check")
    public String healthCheck() {
        return "OK";
    }

    @GetMapping("/test/nvidia")
    public String testNvidia() {

        AIResponse response = aiService.generate(

                AIRequest.builder()
                        .provider(AIProviderType.NVIDIA)
                        .model("nvidia/nemotron-3-nano-omni-30b-a3b-reasoning")
                        .systemPrompt("You are a helpful AI assistant.")
                        .userPrompt("Introduce yourself in one sentence.")
                        .temperature(0.6)
                        .maxTokens(1024)
                        .stream(false)
                        .build()

        );

        return response.getContent();

        }
    }
