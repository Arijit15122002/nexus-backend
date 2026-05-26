package com.arijit.nexus_backend.memory.entity;


import com.arijit.nexus_backend.ai.config.PGvectorType;
import com.arijit.nexus_backend.conversation.entity.Conversation;
import com.pgvector.PGvector;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "memory_summaries")
public class MemorySummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @org.hibernate.annotations.Type(PGvectorType.class)
    @Column(columnDefinition = "vector(3072)")
    private PGvector embedding;

    private Integer importanceScore;

    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }


}
