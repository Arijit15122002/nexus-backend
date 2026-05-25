package com.arijit.nexus_backend.message.entity;

import com.arijit.nexus_backend.ai.config.PGvectorType;
import com.arijit.nexus_backend.conversation.entity.Conversation;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.pgvector.PGvector;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private MessageRole role;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createdAt;
    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id")
    @JsonBackReference
    private Conversation conversation;

    @org.hibernate.annotations.Type(PGvectorType.class)
    @Column(columnDefinition = "vector(3072)")
    private PGvector embedding;

    @Enumerated(EnumType.STRING)
    @Column(name = "memory_type")
    private MemoryType memoryType;

    @Column(name = "importance_score")
    private Integer importanceScore;
}