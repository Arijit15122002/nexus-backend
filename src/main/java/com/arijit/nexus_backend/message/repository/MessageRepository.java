package com.arijit.nexus_backend.message.repository;

import com.arijit.nexus_backend.conversation.entity.Conversation;
import com.arijit.nexus_backend.message.entity.Message;
import com.pgvector.PGvector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByConversationOrderByCreatedAtAsc(Conversation conversation);

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
        ORDER BY embedding <=> CAST(:embedding AS vector)
        LIMIT :limit
        """,
            nativeQuery = true
    )
    List<Message> findSimilarMessages(
            @Param("embedding") String embedding,
            @Param("conversationId") Long conversationId,
            @Param("currentMessageId") Long currentMessageId,
            @Param("limit") int limit
    );

}
