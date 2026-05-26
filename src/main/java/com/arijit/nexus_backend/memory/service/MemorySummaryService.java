package com.arijit.nexus_backend.memory.service;

import com.arijit.nexus_backend.memory.entity.MemorySummary;
import com.arijit.nexus_backend.memory.repository.MemorySummaryRepository;
import com.pgvector.PGvector;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemorySummaryService {

    private final MemorySummaryRepository memorySummaryRepository;

    @Transactional
    public MemorySummary saveSummary(MemorySummary summary) {
        return memorySummaryRepository.save(summary);
    }

    @Transactional(readOnly = true)
    public List<MemorySummary> findRelevantSummaries(
            PGvector embedding,
            Long conversationId,
            int limit
    ) {

        return memorySummaryRepository
                .findRelevantSummaries(
                        embedding.toString(),
                        conversationId,
                        limit
                );

    }

}
