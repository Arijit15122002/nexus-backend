package com.arijit.nexus_backend.memory.repository;

import com.arijit.nexus_backend.memory.entity.MemorySummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemorySummaryRepository extends JpaRepository<MemorySummary, Long> {

    @Query(
            value = """
        SELECT *
        FROM memory_summaries
        
        WHERE conversation_id = :conversationId
        
        ORDER BY
        (
            (
                1 - (
                    embedding <=> CAST(:embedding AS vector)
                )
            ) * 0.8
        )
        +
        (
            (
                importance_score / 10.0
            ) * 0.2
        )
        DESC
        
        LIMIT :limit
        """,
            nativeQuery = true
    )
    List<MemorySummary> findRelevantSummaries(

            @Param("embedding")
            String embedding,

            @Param("conversationId")
            Long conversationId,

            @Param("limit")
            int limit

    );

}
