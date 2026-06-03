package com.arijit.nexus_backend.ai.orchestrator;

import com.arijit.nexus_backend.ai.classifier.dto.MemoryClassificationResult;
import com.arijit.nexus_backend.ai.classifier.service.MemoryClassifierService;
import com.arijit.nexus_backend.ai.embedding.service.EmbeddingService;
import com.arijit.nexus_backend.ai.executor.dto.ExecutionContext;
import com.arijit.nexus_backend.ai.executor.entity.ExecutionIntent;
import com.arijit.nexus_backend.ai.executor.service.CapabilityExecutionService;
import com.arijit.nexus_backend.ai.executor.service.ContextAssemblyService;
import com.arijit.nexus_backend.ai.executor.service.ExecutionIntentDetectionService;
import com.arijit.nexus_backend.ai.prompt.service.PromptEngineeringService;
import com.arijit.nexus_backend.ai.stream.dto.StreamingChunk;
import com.arijit.nexus_backend.ai.stream.entity.StreamingChunkType;
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
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatOrchestratorService {

    private final EmbeddingService embeddingService;

    private final ConversationService conversationService;

    private final MessageService messageService;

    private final MemoryClassifierService memoryClassifierService;

    private final MemoryConsolidationService
            memoryConsolidationService;

    private final MemorySummaryService memorySummaryService;

    private final ToolRoutingService toolRoutingService;

    private final PromptEngineeringService
            promptEngineeringService;

    private final CapabilityExecutionService
            capabilityExecutionService;

    private final ContextAssemblyService
            contextAssemblyService;

    private final ExecutionIntentDetectionService
            executionIntentDetectionService;

    public Flux<StreamingChunk> chat(

            String prompt,

            Long conversationId,

            User user

    ) {

        if (
                prompt == null
                        || prompt.trim().isEmpty()
        ) {

            return Flux.just(

                    StreamingChunk.builder()
                            .type(StreamingChunkType.ERROR)
                            .content("Prompt cannot be empty.")
                            .completed(true)
                            .build()

            );

        }

        return Flux.defer(() -> {

            String trimmedPrompt =
                    prompt.trim();

            // =========================
            // CONVERSATION
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
                        conversationService.createConversation(
                                newConversation
                        );

            }

            // =========================
            // USER CLASSIFICATION
            // =========================

            MemoryClassificationResult userClassification =
                    memoryClassifierService.classify(
                            trimmedPrompt
                    );

            // =========================
            // USER EMBEDDING
            // =========================

            PGvector userEmbedding =
                    embeddingService.generateEmbedding(
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
                                    userClassification.getMemoryType()
                            )
                            .importanceScore(
                                    userClassification.getImportanceScore()
                            )
                            .build();

            userMessage =
                    messageService.saveMessage(
                            userMessage
                    );

            // =========================
            // MEMORY RETRIEVAL
            // =========================

            List<Message> recentMessages =
                    messageService.getRecentMessages(
                            conversation.getId(),
                            10
                    );

            List<Message> relevantMessages =
                    messageService.findRelevantMessages(
                            userEmbedding,
                            conversation.getId(),
                            userMessage.getId(),
                            5
                    );

            List<MemorySummary> relevantSummaries =
                    memorySummaryService.findRelevantSummaries(
                            userEmbedding,
                            conversation.getId(),
                            3
                    );

            // =========================
            // CONTEXT ASSEMBLY
            // =========================

            String assembledContext =
                    contextAssemblyService.assembleContext(

                            buildRelevantMessagesText(
                                    relevantMessages
                            ),

                            buildRecentMessagesText(
                                    recentMessages
                            ),

                            buildSummaryText(
                                    relevantSummaries
                            )

                    );

            // =========================
            // CAPABILITY DETECTION
            // =========================

            CapabilityDetectionResult detectionResult =
                    toolRoutingService.route(
                            trimmedPrompt
                    );

            ExecutionIntent executionIntent =
                    executionIntentDetectionService.detect(
                            trimmedPrompt
                    );

            log.info(
                    "Intent={}",
                    executionIntent
            );

            log.info(
                    "Capability={} | Domain={}",

                    detectionResult.getCapability(),

                    detectionResult.getDomain()
            );

            // =========================
            // PROMPT ENGINEERING
            // =========================

            String finalPrompt =
                    promptEngineeringService.buildPrompt(

                            detectionResult.getCapability(),

                            executionIntent,

                            trimmedPrompt,

                            assembledContext

                    );

            log.info(
                    "FINAL PROMPT:\n{}",
                    finalPrompt
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
                                    detectionResult.getCapability()
                            )

                            .domain(
                                    detectionResult.getDomain()
                            )

                            .executionMode(

                                    capabilityExecutionService
                                            .resolveExecutionMode(
                                                    detectionResult.getCapability()
                                            )

                            )

                            .executionIntent(
                                    executionIntent
                            )

                            .build();

            // =========================
            // AI RESPONSE BUFFER
            // =========================

            AtomicReference<String> aiResponseBuffer =
                    new AtomicReference<>("");

            Conversation finalConversation =
                    conversation;

            // =========================
            // METADATA CHUNK
            // =========================

            StreamingChunk metadataChunk =
                    StreamingChunk.builder()

                            .type(
                                    StreamingChunkType.METADATA
                            )

                            .metadata(
                                    Map.of(

                                            "capability",
                                            detectionResult.getCapability().name(),

                                            "domain",
                                            detectionResult.getDomain().name(),

                                            "conversationId",
                                            finalConversation.getId()

                                    )
                            )

                            .completed(false)

                            .build();

            // =========================
            // EXECUTION
            // =========================

            Flux<StreamingChunk> executionFlux =

                    capabilityExecutionService

                            .execute(executionContext)

                            .doOnNext(chunk -> {

                                if (
                                        chunk.getType()
                                                == StreamingChunkType.TEXT
                                                &&
                                                chunk.getContent() != null
                                ) {

                                    aiResponseBuffer.updateAndGet(

                                            existing ->
                                                    existing
                                                            + chunk.getContent()

                                    );

                                }

                            })

                            .doOnComplete(() -> {

                                Schedulers.boundedElastic()
                                        .schedule(() -> {

                                            try {

                                                String aiResponse =
                                                        aiResponseBuffer
                                                                .get()
                                                                .trim();

                                                if (
                                                        aiResponse.isBlank()
                                                ) {
                                                    return;
                                                }

                                                // =========================
                                                // AI CLASSIFICATION
                                                // =========================

                                                MemoryClassificationResult aiClassification =
                                                        memoryClassifierService
                                                                .classify(
                                                                        aiResponse
                                                                );

                                                // =========================
                                                // AI EMBEDDING
                                                // =========================

                                                PGvector aiEmbedding = null;

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

                                                                .content(
                                                                        aiResponse
                                                                )

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
                                                                &&
                                                                messageCount % 20 == 0
                                                ) {

                                                    memoryConsolidationService
                                                            .consolidateConversationMemory(
                                                                    finalConversation
                                                            );

                                                }

                                            }

                                            catch (Exception e) {

                                                log.error(
                                                        "Post-processing failed",
                                                        e
                                                );

                                            }

                                        });

                            })

                            .onErrorResume(error -> {

                                log.error(
                                        "Execution failed",
                                        error
                                );

                                return Flux.just(

                                        StreamingChunk.builder()

                                                .type(
                                                        StreamingChunkType.ERROR
                                                )

                                                .content(
                                                        "AI execution failed."
                                                )

                                                .completed(true)

                                                .build()

                                );

                            })

                            .subscribeOn(
                                    Schedulers.boundedElastic()
                            );

            return Flux.concat(

                    Flux.just(metadataChunk),

                    executionFlux

            );

        });

    }

    // =========================
    // RECENT MESSAGES
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
    // RELEVANT MESSAGES
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
    // MEMORY SUMMARIES
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