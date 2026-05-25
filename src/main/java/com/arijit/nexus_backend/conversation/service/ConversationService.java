package com.arijit.nexus_backend.conversation.service;

import com.arijit.nexus_backend.conversation.entity.Conversation;
import com.arijit.nexus_backend.conversation.repository.ConversationRepository;
import com.arijit.nexus_backend.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                        new RuntimeException("Conversioni not found"));
    }

}
