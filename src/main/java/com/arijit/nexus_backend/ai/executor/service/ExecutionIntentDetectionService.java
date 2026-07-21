package com.arijit.nexus_backend.ai.executor.service;

import com.arijit.nexus_backend.ai.executor.entity.ExecutionIntent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExecutionIntentDetectionService {

    private final IntentClassificationService intentClassificationService;

    public ExecutionIntent detect(String message) {

        try {

            return intentClassificationService.classify(message);

        }

        catch (Exception ignored) {

            return detectByKeywords(message);

        }

    }

    private ExecutionIntent detectByKeywords(String message) {

        String lower = message.toLowerCase();

        // =========================
        // DSA
        // =========================

        if (

                lower.contains("leetcode")
                        || lower.contains("two sum")
                        || lower.contains("algorithm")
                        || lower.contains("binary search")
                        || lower.contains("linked list")
                        || lower.contains("graph")
                        || lower.contains("tree")
                        || lower.contains("dynamic programming")
                        || lower.contains("dsa")

        ) {

            return ExecutionIntent.DSA;

        }

        // =========================
        // PROJECT GENERATION
        // =========================

        if (

                lower.contains("complete project")
                        || lower.contains("rest api")
                        || lower.contains("microservice")
                        || lower.contains("full stack")
                        || lower.contains("e-commerce")
                        || lower.contains("saas")
                        || lower.contains("authentication")
                        || lower.contains("jwt authentication")

        ) {

            return ExecutionIntent.PROJECT_GENERATION;

        }

        // =========================
        // SINGLE FILE
        // =========================

        if (

                lower.contains("entity")
                        || lower.contains("repository")
                        || lower.contains("service")
                        || lower.contains("controller")
                        || lower.contains("dto")
                        || lower.contains(".java")

        ) {

            return ExecutionIntent.SINGLE_FILE;

        }

        // =========================
        // RESEARCH
        // =========================

        if (

                lower.contains("compare")
                        || lower.contains("comparison")
                        || lower.contains(" vs ")

        ) {

            return ExecutionIntent.RESEARCH;

        }

        // =========================
        // ROADMAP
        // =========================

        if (

                lower.contains("roadmap")
                        || lower.contains("study plan")
                        || lower.contains("learning path")

        ) {

            return ExecutionIntent.ROADMAP;

        }

        // =========================
        // ARCHITECTURE
        // =========================

        if (

                lower.contains("architecture")
                        || lower.contains("system design")
                        || lower.contains("scalable system")

        ) {

            return ExecutionIntent.ARCHITECTURE;

        }

        // =========================
        // AI DESIGN
        // =========================

        if (

                lower.contains("ai agent")
                        || lower.contains("rag")
                        || lower.contains("orchestrator")
                        || lower.contains("llm")
                        || lower.contains("prompt engineering")
                        || lower.contains("vector database")

        ) {

            return ExecutionIntent.AI_DESIGN;

        }

        return ExecutionIntent.CHAT;

    }

}