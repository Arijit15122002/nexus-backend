package com.arijit.nexus_backend.ai.provider.service;

import com.arijit.nexus_backend.ai.executor.dto.ExecutionContext;
import com.arijit.nexus_backend.ai.provider.dto.AIRequest;
import com.arijit.nexus_backend.ai.provider.model.AIProviderType;
import com.arijit.nexus_backend.ai.tool.entity.ToolDomain;
import org.springframework.stereotype.Service;

@Service
public class AIModelRoutingService {

    public AIRequest route(ExecutionContext context) {

        if (context.getDomain() == ToolDomain.CODE) {
            return buildCodingRequest(context);
        }

        return buildChatRequest(context);
    }

    private AIRequest buildChatRequest(
            ExecutionContext context
    ) {

        return AIRequest.builder()

                .provider(AIProviderType.NVIDIA)

                .model("nvidia/nemotron-3-nano-omni-30b-a3b-reasoning")

                .systemPrompt("""
                        You are ORKA AI Assistant.

                        Answer clearly, accurately and professionally.
                        """)

                .userPrompt(context.getFinalPrompt())

                .temperature(0.6)

                .maxTokens(8192)

                .stream(false)

                .build();
    }

    private AIRequest buildCodingRequest(
            ExecutionContext context
    ) {

        return AIRequest.builder()

                .provider(AIProviderType.NVIDIA)

                .model("z-ai/glm-5.2")

                .systemPrompt("""
                        You are ORKA Senior Software Engineer.

                        Follow output format EXACTLY.

                        Every file MUST begin with:

                        FILE: relative/path/FileName.ext
                        LANGUAGE: language

                        Never wrap files inside markdown.

                        Return raw file contents only.

                        Do not explain your solution.
                        """)

                .userPrompt(context.getFinalPrompt())

                .temperature(0.5)

                .maxTokens(16384)

                .stream(false)

                .build();
    }

}