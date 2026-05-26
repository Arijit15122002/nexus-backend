package com.arijit.nexus_backend.ai.orchestrator;

import com.arijit.nexus_backend.ai.classifier.dto.MemoryClassificationResult;
import com.arijit.nexus_backend.ai.classifier.service.MemoryClassifierService;
import com.arijit.nexus_backend.ai.embedding.service.EmbeddingService;
import com.arijit.nexus_backend.ai.executor.dto.ExecutionContext;
import com.arijit.nexus_backend.ai.executor.service.CapabilityExecutionService;
import com.arijit.nexus_backend.ai.executor.service.ContextAssemblyService;
import com.arijit.nexus_backend.ai.prompt.service.PromptEngineeringService;
import com.arijit.nexus_backend.ai.tool.dto.CapabilityDetectionResult;
import com.arijit.nexus_backend.ai.tool.service.ToolRoutingService;
import com.arijit.nexus_backend.conversation.entity.Conversation;
import com.arijit.nexus_backend.conversation.service.ConversationService;
import com.arijit.nexus_backend.memory.entity.MemorySummary;
import com.arijit.nexus_backend.memory.service.MemoryConsolidationService;
import com.arijit.nexus_backend.memory.service.MemorySummaryService;
import com.arijit.nexus_backend.message.entity.Message;
import com.arijit.nexus_backend.message.entity.MessageRole;
import com.arijit.nexus_backend.message.service.MessageService;
import com.arijit.nexus_backend.user.entity.User;
import com.pgvector.PGvector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatOrchestratorService {

    private final EmbeddingService embeddingService;

    private final ConversationService conversationService;

    private final MessageService messageService;

    private final MemoryClassifierService memoryClassifierService;

    private final MemoryConsolidationService memoryConsolidationService;

    private final MemorySummaryService memorySummaryService;

    private final ToolRoutingService toolRoutingService;

    private final PromptEngineeringService promptEngineeringService;

    private final CapabilityExecutionService capabilityExecutionService;

    private final ContextAssemblyService contextAssemblyService;

    public Flux<String> chat(

            String prompt,

            Long conversationId,

            User user

    ) {

        if (
                prompt == null
                        || prompt.trim().isEmpty()
        ) {

            return Flux.just(
                    "Prompt cannot be empty."
            );

        }

        String trimmedPrompt =
                prompt.trim();

        return Flux.defer(() -> {

                    // =========================
                    // CONVERSATION
                    // =========================

                    Conversation conversation;

                    if (conversationId != null) {

                        conversation =
                                conversationService
                                        .getConversation(
                                                conversationId,
                                                user
                                        );

                    }

                    else {

                        Conversation newConversation =
                                Conversation.builder()

                                        .title(
                                                trimmedPrompt.substring(
                                                        0,
                                                        Math.min(
                                                                trimmedPrompt.length(),
                                                                30
                                                        )
                                                )
                                        )

                                        .user(user)

                                        .build();

                        conversation =
                                conversationService
                                        .createConversation(
                                                newConversation
                                        );

                    }

                    // =========================
                    // USER CLASSIFICATION
                    // =========================

                    MemoryClassificationResult
                            userClassification =

                            memoryClassifierService
                                    .classify(trimmedPrompt);

                    // =========================
                    // USER EMBEDDING
                    // =========================

                    PGvector userEmbedding =
                            embeddingService
                                    .generateEmbedding(
                                            trimmedPrompt
                                    );

                    // =========================
                    // SAVE USER MESSAGE
                    // =========================

                    Message userMessage =
                            Message.builder()

                                    .content(trimmedPrompt)

                                    .role(MessageRole.USER)

                                    .conversation(conversation)

                                    .embedding(userEmbedding)

                                    .memoryType(
                                            userClassification
                                                    .getMemoryType()
                                    )

                                    .importanceScore(
                                            userClassification
                                                    .getImportanceScore()
                                    )

                                    .build();

                    userMessage =
                            messageService
                                    .saveMessage(userMessage);

                    // =========================
                    // MEMORY RETRIEVAL
                    // =========================

                    List<Message> recentMessages =
                            messageService
                                    .getRecentMessages(
                                            conversation.getId(),
                                            10
                                    );

                    List<Message> relevantMessages =
                            messageService
                                    .findRelevantMessages(

                                            userEmbedding,

                                            conversation.getId(),

                                            userMessage.getId(),

                                            5

                                    );

                    List<MemorySummary> relevantSummaries =
                            memorySummaryService
                                    .findRelevantSummaries(

                                            userEmbedding,

                                            conversation.getId(),

                                            3

                                    );

                    // =========================
                    // BUILD MEMORY CONTEXT
                    // =========================

                    String recentMessagesText =
                            buildRecentMessagesText(
                                    recentMessages
                            );

                    String relevantMessagesText =
                            buildRelevantMessagesText(
                                    relevantMessages
                            );

                    String summaryText =
                            buildSummaryText(
                                    relevantSummaries
                            );

                    String assembledContext =
                            contextAssemblyService
                                    .assembleContext(

                                            relevantMessagesText,

                                            recentMessagesText,

                                            summaryText

                                    );

                    // =========================
                    // CAPABILITY DETECTION
                    // =========================

                    CapabilityDetectionResult
                            detectionResult =

                            toolRoutingService
                                    .route(trimmedPrompt);

                    // =========================
                    // PROMPT ENGINEERING
                    // =========================

                    String finalPrompt =
                            promptEngineeringService
                                    .buildPrompt(

                                            detectionResult
                                                    .getCapability(),

                                            trimmedPrompt,

                                            assembledContext

                                    );

                    // =========================
                    // EXECUTION CONTEXT
                    // =========================

                    ExecutionContext executionContext =
                            ExecutionContext.builder()

                                    .userMessage(trimmedPrompt)

                                    .memoryContext(
                                            assembledContext
                                    )

                                    .finalPrompt(finalPrompt)

                                    .capability(
                                            detectionResult
                                                    .getCapability()
                                    )

                                    .domain(
                                            detectionResult
                                                    .getDomain()
                                    )

                                    .executionMode(

                                            capabilityExecutionService
                                                    .resolveExecutionMode(

                                                            detectionResult
                                                                    .getCapability()

                                                    )

                                    )

                                    .build();

                    // =========================
                    // STREAM RESPONSE
                    // =========================

                    StringBuffer aiResponseBuffer =
                            new StringBuffer();

                    AtomicBoolean isError =
                            new AtomicBoolean(false);

                    Conversation finalConversation =
                            conversation;

                    return capabilityExecutionService

                            .execute(executionContext)

                            .onErrorResume(error -> {

                                log.error(
                                        "Execution failed: {}",
                                        error.getMessage(),
                                        error
                                );

                                isError.set(true);

                                return Flux.just(
                                        "\n[AI is temporarily unavailable.]"
                                );

                            })

                            .doOnNext(chunk -> {

                                if (
                                        chunk != null
                                                && !chunk.isBlank()
                                ) {

                                    aiResponseBuffer
                                            .append(chunk);

                                }

                            })

                            .doOnComplete(() -> {

                                if (isError.get()) {
                                    return;
                                }

                                try {

                                    String aiResponse =
                                            aiResponseBuffer
                                                    .toString()
                                                    .trim();

                                    if (aiResponse.isBlank()) {
                                        return;
                                    }

                                    // =========================
                                    // AI CLASSIFICATION
                                    // =========================

                                    MemoryClassificationResult
                                            aiClassification =

                                            memoryClassifierService
                                                    .classify(
                                                            aiResponse
                                                    );

                                    // =========================
                                    // AI EMBEDDING
                                    // =========================

                                    PGvector aiEmbedding =
                                            null;

                                    try {

                                        aiEmbedding =
                                                embeddingService
                                                        .generateEmbedding(
                                                                aiResponse
                                                        );

                                    }

                                    catch (Exception e) {

                                        log.warn(
                                                "AI embedding failed: {}",
                                                e.getMessage()
                                        );

                                    }

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

                                                    .embedding(aiEmbedding)

                                                    .memoryType(
                                                            aiClassification
                                                                    .getMemoryType()
                                                    )

                                                    .importanceScore(
                                                            aiClassification
                                                                    .getImportanceScore()
                                                    )

                                                    .build();

                                    messageService
                                            .saveMessage(aiMessage);

                                    // =========================
                                    // MEMORY CONSOLIDATION
                                    // =========================

                                    long messageCount =
                                            messageService
                                                    .countConversationMessages(
                                                            finalConversation
                                                    );

                                    if (
                                            messageCount >= 20
                                                    && messageCount % 20 == 0
                                    ) {

                                        Schedulers
                                                .boundedElastic()
                                                .schedule(() -> {

                                                    try {

                                                        memoryConsolidationService
                                                                .consolidateConversationMemory(
                                                                        finalConversation
                                                                );

                                                    }

                                                    catch (Exception e) {

                                                        log.error(
                                                                "Memory consolidation failed",
                                                                e
                                                        );

                                                    }

                                                });

                                    }

                                }

                                catch (Exception e) {

                                    log.error(
                                            "doOnComplete failed",
                                            e
                                    );

                                }

                            })

                            .subscribeOn(
                                    Schedulers.boundedElastic()
                            );

                })

                .onErrorResume(error -> {

                    log.error(
                            "Chat orchestration failed",
                            error
                    );

                    return Flux.just(
                            "\n[An error occurred while processing your request.]"
                    );

                });

    }

    // =========================
    // RECENT
    // =========================

    private String buildRecentMessagesText(
            List<Message> messages
    ) {

        StringBuilder sb =
                new StringBuilder();

        for (Message message : messages) {

            sb.append(message.getRole())

                    .append(": ")

                    .append(message.getContent())

                    .append("\n");

        }

        return sb.toString();

    }

    // =========================
    // RELEVANT
    // =========================

    private String buildRelevantMessagesText(
            List<Message> messages
    ) {

        StringBuilder sb =
                new StringBuilder();

        for (Message message : messages) {

            sb.append(message.getRole())

                    .append(": ")

                    .append(message.getContent())

                    .append("\n");

        }

        return sb.toString();

    }

    // =========================
    // SUMMARIES
    // =========================

    private String buildSummaryText(
            List<MemorySummary> summaries
    ) {

        StringBuilder sb =
                new StringBuilder();

        for (MemorySummary summary : summaries) {

            sb.append(summary.getSummary())

                    .append("\n");

        }

        return sb.toString();

    }

}