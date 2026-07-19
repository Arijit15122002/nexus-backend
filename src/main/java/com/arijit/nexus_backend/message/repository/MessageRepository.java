package com.arijit.nexus_backend.message.repository;

import com.arijit.nexus_backend.conversation.entity.Conversation;
import com.arijit.nexus_backend.message.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository
        extends JpaRepository<Message, Long> {

    List<Message> findByConversationOrderByCreatedAtAsc(
            Conversation conversation
    );

    @Query(
            value = """
        SELECT * FROM (
            SELECT *
            FROM messages
            WHERE conversation_id = :conversationId
            ORDER BY created_at DESC
            LIMIT :limit
        ) sub
        ORDER BY created_at ASC
        """,
            nativeQuery = true
    )
    List<Message> findTopMessagesByConversation(

            @Param("conversationId")
            Long conversationId,

            @Param("limit")
            int limit

    );

    @Query(
            value = """
        SELECT *
        FROM messages
        WHERE conversation_id = :conversationId
        AND id != :currentMessageId
        
        ORDER BY
        (
            (
                1 - (
                    embedding <=> CAST(:embedding AS vector)
                )
            ) * 0.7
        )
        +
        (
            (
                importance_score / 10.0
            ) * 0.2
        )
        +
        (
            CASE
                WHEN created_at > NOW() - INTERVAL '1 day'
                THEN 0.1
                ELSE 0
            END
        )
        DESC
        
        LIMIT :limit
        """,
            nativeQuery = true
    )
    List<Message> findRelevantMessages(

            @Param("embedding")
            String embedding,

            @Param("conversationId")
            Long conversationId,

            @Param("currentMessageId")
            Long currentMessageId,

            @Param("limit")
            int limit

    );

    long countByConversation(Conversation conversation);

    Page<Message> findByConversationIdOrderByCreatedAtDesc(
            Long conversationId,
            Pageable pageable
    );

}