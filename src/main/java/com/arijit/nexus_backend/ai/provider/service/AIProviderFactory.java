package com.arijit.nexus_backend.ai.provider.service;

import com.arijit.nexus_backend.ai.provider.model.AIProviderType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AIProviderFactory {

    private final List<AIProvider> providers;

    public AIProvider getProvider(AIProviderType providerType) {

        return providers.stream()
                .filter(provider -> provider.getProvider() == providerType)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No AI Provider found for : " + providerType
                        )
                );
    }
}