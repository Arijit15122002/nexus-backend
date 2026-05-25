package com.arijit.nexus_backend.ai.orchestrator;

import com.arijit.nexus_backend.ai.classifier.dto.MemoryClassificationResult;
import com.arijit.nexus_backend.ai.classifier.service.MemoryClassifierService;
import com.arijit.nexus_backend.ai.embedding.service.EmbeddingService;
import com.arijit.nexus_backend.ai.provider.gemini.service.GeminiService;
import com.arijit.nexus_backend.conversation.entity.Conversation;
import com.arijit.nexus_backend.conversation.service.ConversationService;
import com.arijit.nexus_backend.message.entity.Message;
import com.arijit.nexus_backend.message.entity.MessageRole;
import com.arijit.nexus_backend.message.service.MessageService;
import com.arijit.nexus_backend.user.entity.User;
import com.pgvector.PGvector;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatOrchestratorService {

    private final GeminiService geminiService;

    private final EmbeddingService embeddingService;

    private final ConversationService conversationService;

    private final MessageService messageService;

    private final MemoryClassifierService memoryClassifierService;

    public Flux<String> chat(
            String prompt,
            Long conversationId,
            User user
    ) {

        // =========================
        // VALIDATE PROMPT
        // =========================

        if (
                prompt == null
                        || prompt.trim().isEmpty()
        ) {

            return Flux.just(
                    "Prompt cannot be empty."
            );

        }

        prompt = prompt.trim();

        // =========================
        // FETCH / CREATE CONVERSATION
        // =========================

        Conversation conversation;

        if (conversationId != null) {

            conversation =
                    conversationService.getConversation(
                            conversationId,
                            user
                    );

        }

        else {

            conversation =
                    Conversation.builder()
                            .title(
                                    prompt.substring(
                                            0,
                                            Math.min(prompt.length(), 30)
                                    )
                            )
                            .user(user)
                            .build();

            conversation =
                    conversationService.createConversation(
                            conversation
                    );

        }

        // =========================
        // CLASSIFY USER MEMORY
        // =========================

        MemoryClassificationResult classification =
                memoryClassifierService.classify(
                        prompt
                );

        // =========================
        // GENERATE USER EMBEDDING
        // =========================

        PGvector userEmbedding =
                embeddingService.generateEmbedding(
                        prompt
                );

        // =========================
        // SAVE USER MESSAGE
        // =========================

        Message userMessage =
                Message.builder()
                        .content(prompt)
                        .role(MessageRole.USER)
                        .conversation(conversation)
                        .embedding(userEmbedding)
                        .memoryType(
                                classification.getMemoryType()
                        )
                        .importanceScore(
                                classification.getImportanceScore()
                        )
                        .build();

        userMessage =
                messageService.saveMessage(
                        userMessage
                );

        // =========================
        // FETCH RECENT MESSAGES
        // =========================

        List<Message> recentMessages =
                messageService.getRecentMessages(
                        conversation.getId(),
                        10
                );

        // =========================
        // FETCH RELEVANT MEMORIES
        // =========================

        List<Message> relevantMessages =
                messageService.findRelevantMessages(
                        userEmbedding,
                        conversation.getId(),
                        userMessage.getId(),
                        5
                );

        // =========================
        // BUILD CONTEXT
        // =========================

        StringBuilder contextBuilder =
                new StringBuilder();

        Set<Long> addedIds =
                new HashSet<>();

        // =========================
        // MEMORY CONTEXT
        // =========================

        contextBuilder.append("""
                SYSTEM MEMORY:
                                
                """);

        for (Message message : relevantMessages) {

            if (
                    message.getId() == null
                            || addedIds.contains(
                            message.getId()
                    )
            ) {

                continue;

            }

            addedIds.add(
                    message.getId()
            );

            contextBuilder
                    .append(message.getRole())
                    .append(": ")
                    .append(message.getContent())
                    .append("\n");

        }

        // =========================
        // RECENT CHAT CONTEXT
        // =========================

        contextBuilder.append("""

                                
                RECENT CONVERSATION:
                                
                """);

        for (Message message : recentMessages) {

            if (
                    message.getId() == null
                            || addedIds.contains(
                            message.getId()
                    )
            ) {

                continue;

            }

            addedIds.add(
                    message.getId()
            );

            contextBuilder
                    .append(message.getRole())
                    .append(": ")
                    .append(message.getContent())
                    .append("\n");

        }

        // =========================
        // CURRENT USER INPUT
        // =========================

        contextBuilder.append("""

                                
                CURRENT USER MESSAGE:
                                
                """);

        contextBuilder.append(prompt);

        String fullContext =
                contextBuilder.toString();

        // =========================
        // STREAM RESPONSE
        // =========================

        StringBuilder aiResponseBuilder =
                new StringBuilder();

        Conversation finalConversation =
                conversation;

        return geminiService
                .generateResponseStream(
                        fullContext
                )

                .doOnNext(chunk -> {

                    if (
                            chunk != null
                                    && !chunk.isBlank()
                    ) {

                        aiResponseBuilder.append(
                                chunk
                        );

                    }

                })

                .doOnComplete(() ->

                        Schedulers.boundedElastic()
                                .schedule(() -> {

                                    try {

                                        String aiResponse =
                                                aiResponseBuilder
                                                        .toString()
                                                        .trim();

                                        // =========================
                                        // EMPTY RESPONSE PROTECTION
                                        // =========================

                                        if (
                                                aiResponse.isEmpty()
                                        ) {

                                            System.out.println(
                                                    "AI response empty. Skipping save."
                                            );

                                            return;

                                        }

                                        // =========================
                                        // CLASSIFY AI MEMORY
                                        // =========================

                                        MemoryClassificationResult aiClassification =
                                                memoryClassifierService.classify(
                                                        aiResponse
                                                );

                                        // =========================
                                        // GENERATE AI EMBEDDING
                                        // =========================

                                        PGvector aiEmbedding =
                                                embeddingService.generateEmbedding(
                                                        aiResponse
                                                );

                                        // =========================
                                        // SAVE AI MESSAGE
                                        // =========================

                                        Message aiMessage =
                                                Message.builder()
                                                        .content(aiResponse)
                                                        .role(
                                                                MessageRole.ASSISTANT
                                                        )
                                                        .conversation(
                                                                finalConversation
                                                        )
                                                        .embedding(
                                                                aiEmbedding
                                                        )
                                                        .memoryType(
                                                                aiClassification.getMemoryType()
                                                        )
                                                        .importanceScore(
                                                                aiClassification.getImportanceScore()
                                                        )
                                                        .build();

                                        messageService.saveMessage(
                                                aiMessage
                                        );

                                    }

                                    catch (Exception e) {

                                        e.printStackTrace();

                                    }

                                })

                )

                .subscribeOn(
                        Schedulers.boundedElastic()
                );

    }

}