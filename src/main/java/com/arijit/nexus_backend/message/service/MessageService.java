package com.arijit.nexus_backend.message.service;

import com.arijit.nexus_backend.conversation.entity.Conversation;
import com.arijit.nexus_backend.conversation.repository.ConversationRepository;
import com.arijit.nexus_backend.message.dto.MessageResponse;
import com.arijit.nexus_backend.message.entity.Message;
import com.arijit.nexus_backend.message.repository.MessageRepository;
import com.arijit.nexus_backend.user.entity.User;
import com.pgvector.PGvector;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;

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

    public Page<MessageResponse> getMessages(
            Long conversationId,
            User user,
            int page,
            int size
    ) {

        Conversation conversation = conversationRepository
                .findByIdAndUser(conversationId, user)
                .orElseThrow(() ->
                        new RuntimeException("Conversation not found"));

        Pageable pageable = PageRequest.of(page, size);

        return messageRepository
                .findByConversationIdOrderByCreatedAtDesc(
                        conversation.getId(),
                        pageable
                )
                .map(message -> new MessageResponse(
                        message.getId(),
                        message.getRole(),
                        message.getContent(),
                        message.getCreatedAt()
                ));
    }

}