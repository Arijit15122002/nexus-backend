package com.arijit.nexus_backend.memory.service;

import com.arijit.nexus_backend.ai.embedding.service.EmbeddingService;
import com.arijit.nexus_backend.ai.provider.gemini.service.GeminiService;
import com.arijit.nexus_backend.conversation.entity.Conversation;
import com.arijit.nexus_backend.memory.entity.MemorySummary;
import com.arijit.nexus_backend.message.entity.Message;
import com.arijit.nexus_backend.message.service.MessageService;
import com.pgvector.PGvector;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemoryConsolidationService {

    private final MessageService messageService;

    private final GeminiService geminiService;

    private final EmbeddingService embeddingService;

    private final MemorySummaryService memorySummaryService;

    public void consolidateConversationMemory(
            Conversation conversation
    ) {

        // =========================
        // FETCH IMPORTANT MEMORIES
        // =========================

        List<Message> messages =
                messageService.getRecentMessages(
                        conversation.getId(),
                        30
                );

        if (messages.isEmpty()) {
            return;
        }

        // =========================
        // BUILD MEMORY TEXT
        // =========================

        StringBuilder memoryBuilder =
                new StringBuilder();

        for (Message message : messages) {

            // ignore temporary memories

            if (
                    message.getImportanceScore() != null
                            && message.getImportanceScore() <= 3
            ) {

                continue;

            }

            memoryBuilder
                    .append(message.getRole())
                    .append(": ")
                    .append(message.getContent())
                    .append("\n");

        }

        String memoryText =
                memoryBuilder.toString().trim();

        if (memoryText.isBlank()) {
            return;
        }

        // =========================
        // BUILD SUMMARY PROMPT
        // =========================

        String summaryPrompt =
                """
                Summarize the following conversation memories
                into a compact long-term memory summary.
                
                Focus on:
                - user identity
                - preferences
                - projects
                - important facts
                - ongoing work
                
                Ignore casual conversation.
                
                Conversation:
                
                """
                        + memoryText;

        // =========================
        // GENERATE SUMMARY
        // =========================

        String summary =
                geminiService.generateResponse(
                        summaryPrompt
                );

        if (
                summary == null
                        || summary.isBlank()
        ) {

            return;

        }

        // =========================
        // GENERATE SUMMARY EMBEDDING
        // =========================

        PGvector embedding =
                embeddingService.generateEmbedding(
                        summary
                );

        // =========================
        // SAVE LONG TERM MEMORY
        // =========================

        MemorySummary memorySummary =
                MemorySummary.builder()
                        .summary(summary)
                        .embedding(embedding)
                        .importanceScore(9)
                        .conversation(conversation)
                        .build();

        memorySummaryService.saveSummary(
                memorySummary
        );

        System.out.println(
                "Memory consolidation completed."
        );

    }

}