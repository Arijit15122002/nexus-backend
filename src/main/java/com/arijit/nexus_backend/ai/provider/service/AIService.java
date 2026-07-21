package com.arijit.nexus_backend.ai.provider.service;

import com.arijit.nexus_backend.ai.provider.dto.AIRequest;
import com.arijit.nexus_backend.ai.provider.dto.AIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AIService {

    private final AIProviderFactory providerFactory;

    public AIResponse generate(AIRequest request) {

        AIProvider provider = providerFactory.getProvider(request.getProvider());

        return provider.generate(request);
    }

}