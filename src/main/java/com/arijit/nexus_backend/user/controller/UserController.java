package com.arijit.nexus_backend.user.controller;

import com.arijit.nexus_backend.user.dto.UserInfo;
import com.arijit.nexus_backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    final UserService userService;

    @GetMapping("/get-username")
    public ResponseEntity<?> getUserName() {
        try{
            UserInfo info = userService.getUserDetails();
            return new ResponseEntity<>(info, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body("Something went wrong: " + e);
        }
    }

}
