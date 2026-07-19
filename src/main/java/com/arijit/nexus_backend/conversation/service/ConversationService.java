package com.arijit.nexus_backend.conversation.service;

import com.arijit.nexus_backend.conversation.entity.Conversation;
import com.arijit.nexus_backend.conversation.repository.ConversationRepository;
import com.arijit.nexus_backend.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
@Transactional
public class ConversationService {

    private final ConversationRepository conversationRepository;

    public Conversation createConversation(Conversation conversation) {
        return conversationRepository.save(conversation);
    }

    public Conversation getConversation(Long id, User user) {
        return conversationRepository.findByIdAndUser(id, user)
                .orElseThrow(() ->
                        new RuntimeException("Conversation not found"));
    }

    public Page<Conversation> getConversations(Long userId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return conversationRepository.findByUserIdOrderByUpdatedAtDesc(userId, pageable);
    }

}
