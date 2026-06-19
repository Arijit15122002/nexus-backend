package com.arijit.nexus_backend.user.service;

import com.arijit.nexus_backend.user.dto.UserInfo;
import com.arijit.nexus_backend.user.entity.User;
import com.arijit.nexus_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    final UserRepository userRepository;

    public UserInfo getUserDetails() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserInfo(user.getUsername(), user.getEmail());

    }

}
