package ma.foodallergyai.foodallergyapi.controller;

import ma.foodallergyai.foodallergyapi.dto.ChatMessageDto;
import ma.foodallergyai.foodallergyapi.dto.ChatRequestDto;
import ma.foodallergyapi.dto.ChatResponseDto;
import ma.foodallergyai.foodallergyapi.model.ChatConversation;
import ma.foodallergyai.foodallergyapi.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/send")
    public ResponseEntity<ChatResponseDto> sendMessage(@RequestBody ChatRequestDto request) {
        try {
            ChatResponseDto response = chatService.processMessage(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ChatResponseDto errorResponse = new ChatResponseDto();
            errorResponse.setSuccess(false);
            errorResponse.setError("Internal server error");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/conversations/{userId}")
    public ResponseEntity<List<ChatConversation>> getUserConversations(@PathVariable UUID userId) {
        try {
            List<ChatConversation> conversations = chatService.getUserConversations(userId);
            return ResponseEntity.ok(conversations);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/conversations/{userId}/{conversationId}/messages")
    public ResponseEntity<List<ChatMessageDto>> getConversationHistory(
            @PathVariable UUID userId,
            @PathVariable UUID conversationId) {
        try {
            List<ChatMessageDto> messages = chatService.getConversationHistory(userId, conversationId);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/conversations/{userId}")
    public ResponseEntity<ChatConversation> createConversation(@PathVariable UUID userId) {
        try {
            // This will be handled by the service when first message is sent
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}