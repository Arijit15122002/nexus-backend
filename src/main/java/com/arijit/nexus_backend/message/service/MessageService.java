package com.arijit.nexus_backend.message.service;

import com.arijit.nexus_backend.conversation.entity.Conversation;
import com.arijit.nexus_backend.message.entity.Message;
import com.arijit.nexus_backend.message.repository.MessageRepository;
import com.pgvector.PGvector;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    @Transactional
    public Message saveMessage(Message message) {

        return messageRepository.save(message);

    }

    @Transactional(readOnly = true)
    public List<Message> getConversationMessage(
            Conversation conversation
    ) {

        return messageRepository
                .findByConversationOrderByCreatedAtAsc(
                        conversation
                );

    }

    @Transactional(readOnly = true)
    public List<Message> getRecentMessages(
            Long conversationId,
            int limit
    ) {

        return messageRepository
                .findTopMessagesByConversation(
                        conversationId,
                        limit
                );

    }

    @Transactional(readOnly = true)
    public List<Message> findRelevantMessages(
            PGvector embedding,
            Long conversationId,
            Long currentMessageId,
            int limit
    ) {
        return messageRepository.findRelevantMessages(
                embedding.toString(),  // ← convert to String
                conversationId,
                currentMessageId,
                limit
        );
    }

    public long countConversationMessages(Conversation conversation) {
        return messageRepository.countByConversation(conversation);
    }

}