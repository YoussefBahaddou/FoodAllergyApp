package ma.foodallergyai.foodallergyapi.repository;

import ma.foodallergyai.foodallergyapi.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {

    List<ChatMessage> findByConversationIdOrderByCreatedAtAsc(UUID conversationId);

    @Query("SELECT m FROM ChatMessage m WHERE m.conversation.id = :conversationId ORDER BY m.createdAt DESC LIMIT :limit")
    List<ChatMessage> findRecentMessagesByConversationId(@Param("conversationId") UUID conversationId, @Param("limit") int limit);
}