package com.arijit.nexus_backend.ai.prompt.service;

import com.arijit.nexus_backend.ai.tool.entity.ToolCapability;
import org.springframework.stereotype.Service;

@Service
public class PromptEngineeringService {

    public String buildPrompt(

            ToolCapability capability,

            String userMessage,

            String memoryContext

    ) {

        return switch (capability) {

            // CODE

            case CODE_GENERATION -> """

                    You are an elite senior software engineer.

                    Generate production-grade code.

                    Follow:
                    - clean architecture
                    - scalability
                    - readability
                    - best practices

                    Memory Context:
                    %s

                    User Request:
                    %s

                    """.formatted(
                    memoryContext,
                    userMessage
            );

            // BUG FIXING

            case BUG_FIXING -> """

                    You are an expert debugging engineer.

                    Analyze:
                    - root cause
                    - stack traces
                    - architecture issues

                    Then provide:
                    - explanation
                    - fix
                    - corrected code

                    Memory Context:
                    %s

                    User Request:
                    %s

                    """.formatted(
                    memoryContext,
                    userMessage
            );

            // ARCHITECTURE

            case ARCHITECTURE_DESIGN -> """

                    You are an expert software architect.

                    Design scalable systems.

                    Focus on:
                    - scalability
                    - clean architecture
                    - distributed systems
                    - maintainability
                    - performance

                    Memory Context:
                    %s

                    User Request:
                    %s

                    """.formatted(
                    memoryContext,
                    userMessage
            );

            // ROADMAP

            case ROADMAP_GENERATION -> """

                    You are an expert mentor.

                    Generate a highly structured roadmap.

                    Include:
                    - phases
                    - milestones
                    - timelines
                    - learning priorities

                    Memory Context:
                    %s

                    User Request:
                    %s

                    """.formatted(
                    memoryContext,
                    userMessage
            );

            // RESUME

            case RESUME_GENERATION -> """

                    You are an expert resume writer.

                    Create:
                    - ATS optimized
                    - professional
                    - concise
                    - impactful resumes

                    Memory Context:
                    %s

                    User Request:
                    %s

                    """.formatted(
                    memoryContext,
                    userMessage
            );

            // RESEARCH

            case WEB_RESEARCH -> """

                    You are an expert researcher.

                    Perform:
                    - deep analysis
                    - comparisons
                    - reasoning
                    - pros and cons

                    Memory Context:
                    %s

                    User Request:
                    %s

                    """.formatted(
                    memoryContext,
                    userMessage
            );

            // AI

            case AI_AGENT_DESIGN -> """

                    You are an expert AI systems architect.

                    Design:
                    - RAG systems
                    - AI agents
                    - orchestration systems
                    - memory systems
                    - scalable AI architectures

                    Memory Context:
                    %s

                    User Request:
                    %s

                    """.formatted(
                    memoryContext,
                    userMessage
            );

            // DEFAULT

            default -> """

                    You are Nexus AI.

                    Be:
                    - intelligent
                    - conversational
                    - helpful
                    - context aware

                    Memory Context:
                    %s

                    User Request:
                    %s

                    """.formatted(
                    memoryContext,
                    userMessage
            );

        };

    }

}