package com.arijit.nexus_backend.conversation.repository;

import com.arijit.nexus_backend.conversation.entity.Conversation;
import com.arijit.nexus_backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Optional<Conversation> findByIdAndUser(Long id, User user);

    Page<Conversation> findByUserIdOrderByUpdatedAtDesc(Long userId, Pageable pageable);

}
