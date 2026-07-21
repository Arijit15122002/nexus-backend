package com.arijit.nexus_backend.ai.provider.service;

import com.arijit.nexus_backend.ai.provider.dto.AIRequest;
import com.arijit.nexus_backend.ai.provider.dto.AIResponse;
import com.arijit.nexus_backend.ai.provider.model.AIProviderType;

public interface AIProvider {

    AIProviderType getProvider();

    AIResponse generate(AIRequest request);

}