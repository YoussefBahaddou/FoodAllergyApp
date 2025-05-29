package ma.foodallergyai.foodallergyapi.service;

import ma.foodallergyai.foodallergyapi.dto.ChatMessageDto;
import ma.foodallergyai.foodallergyapi.dto.ChatRequestDto;
import ma.foodallergyai.foodallergyapi.dto.ChatResponseDto;
import ma.foodallergyai.foodallergyapi.model.ChatConversation;
import ma.foodallergyai.foodallergyapi.model.ChatMessage;
import ma.foodallergyai.foodallergyapi.repository.ChatConversationRepository;
import ma.foodallergyai.foodallergyapi.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChatService {

    @Autowired
    private ChatConversationRepository conversationRepository;

    @Autowired
    private ChatMessageRepository messageRepository;

    @Autowired
    private AIService aiService;

    @Autowired
    private ChatContextService contextService;

    private final java.util.Map<String, String> responses = new java.util.HashMap<>();
    
    public ChatService() {
        // Initialize with some basic responses
        responses.put("allergy", "Food allergies occur when your immune system reacts to certain proteins in food. Common allergens include peanuts, tree nuts, milk, eggs, wheat, soy, fish, and shellfish.");
        responses.put("symptoms", "Common allergy symptoms include hives, itching, swelling, difficulty breathing, and in severe cases, anaphylaxis. If you experience these symptoms, seek medical attention immediately.");
        responses.put("prevention", "To prevent allergic reactions, always read food labels carefully, inform restaurant staff about your allergies, and carry emergency medication if prescribed.");
        responses.put("treatment", "The best treatment is strict avoidance of the allergen. Always carry prescribed emergency medication (like an EpiPen) and know how to use it.");
    }

    public ChatResponseDto processMessage(ChatRequestDto request) {
        try {
            // Validate input
            if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
                ChatResponseDto errorResponse = new ChatResponseDto();
                errorResponse.setSuccess(false);
                errorResponse.setError("Message cannot be empty");
                return errorResponse;
            }

            // Check if message is relevant to food allergies
            if (!contextService.isRelevantToFoodAllergies(request.getMessage())) {
                ChatResponseDto redirectResponse = new ChatResponseDto();
                redirectResponse.setBotResponse(contextService.getTopicRedirectionMessage());
                redirectResponse.setMessageType("topic_redirect");
                return redirectResponse;
            }

            // Get or create conversation
            ChatConversation conversation = getOrCreateConversation(request.getUserId(), request.getConversationId());

            // Save user message
            ChatMessage userMessage = new ChatMessage(conversation, request.getMessage(), true);
            messageRepository.save(userMessage);

            // Build context for AI
            String userContext = contextService.buildUserContext(request.getUserId());

            // Generate AI response
            String aiResponse = aiService.generateResponse(request.getMessage(), userContext);

            // Save AI response
            ChatMessage botMessage = new ChatMessage(conversation, aiResponse, false);
            messageRepository.save(botMessage);

            // Update conversation timestamp
            conversation.setUpdatedAt(LocalDateTime.now());
            conversationRepository.save(conversation);

            // Return response
            ChatResponseDto response = new ChatResponseDto(conversation.getId(), aiResponse);
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            ChatResponseDto errorResponse = new ChatResponseDto();
            errorResponse.setSuccess(false);
            errorResponse.setError("Sorry, I encountered an error. Please try again.");
            return errorResponse;
        }
    }

    public List<ChatMessageDto> getConversationHistory(UUID userId, UUID conversationId) {
        ChatConversation conversation = conversationRepository.findByUserIdAndId(userId, conversationId)
                .orElse(null);

        if (conversation == null) {
            return List.of();
        }

        List<ChatMessage> messages = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);

        return messages.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<ChatConversation> getUserConversations(UUID userId) {
        return conversationRepository.findByUserIdOrderByUpdatedAtDesc(userId);
    }

    private ChatConversation getOrCreateConversation(UUID userId, UUID conversationId) {
        if (conversationId != null) {
            return conversationRepository.findByUserIdAndId(userId, conversationId)
                    .orElseThrow(() -> new RuntimeException("Conversation not found"));
        }

        // Create new conversation
        ChatConversation newConversation = new ChatConversation(userId);
        return conversationRepository.save(newConversation);
    }

    private ChatMessageDto convertToDto(ChatMessage message) {
        return new ChatMessageDto(
                message.getId(),
                message.getConversation().getId(),
                message.getMessageText(),
                message.getIsUserMessage(),
                message.getMessageType(),
                message.getCreatedAt()
        );
    }

    public String processMessage(String message) {
        message = message.toLowerCase();
        
        // Check for keywords and return appropriate response
        for (java.util.Map.Entry<String, String> entry : responses.entrySet()) {
            if (message.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        
        // Default response if no keywords are found
        return "I'm your food allergy assistant. I can help you with information about allergies, symptoms, prevention, and treatment. What would you like to know?";
    }
}
