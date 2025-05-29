package ma.foodallergyai.foodallergyapi.repository;

import ma.foodallergyai.foodallergyapi.model.ChatConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatConversationRepository extends JpaRepository<ChatConversation, UUID> {

    List<ChatConversation> findByUserIdOrderByUpdatedAtDesc(UUID userId);

    Optional<ChatConversation> findByUserIdAndId(UUID userId, UUID conversationId);

    @Query("SELECT c FROM ChatConversation c WHERE c.userId = :userId ORDER BY c.updatedAt DESC LIMIT 1")
    Optional<ChatConversation> findLatestByUserId(@Param("userId") UUID userId);
}