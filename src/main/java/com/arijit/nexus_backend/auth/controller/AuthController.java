package com.arijit.nexus_backend.auth.controller;

import com.arijit.nexus_backend.auth.dto.AuthResponse;
import com.arijit.nexus_backend.auth.dto.LoginRequest;
import com.arijit.nexus_backend.auth.dto.RegisterRequest;
import com.arijit.nexus_backend.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try{
            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.unprocessableContent().body("Exception found in your request body" + e);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try{
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.unprocessableContent().body("Exception occurred while logging you in" + e);
        }
    }

}
