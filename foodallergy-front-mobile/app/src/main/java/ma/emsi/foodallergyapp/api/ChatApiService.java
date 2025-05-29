package ma.emsi.foodallergyapp.api;

import ma.emsi.foodallergyapp.model.ChatMessage;
import ma.emsi.foodallergyapp.model.ChatRequest;
import ma.emsi.foodallergyapp.model.ChatResponse;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;
import java.util.UUID;

public interface ChatApiService {

    @POST("api/chat/message")
    Call<ChatResponse> sendMessage(@Body ChatRequest request);

    @GET("api/chat/conversations/{userId}/{conversationId}/messages")
    Call<List<ChatMessage>> getConversationHistory(
            @Path("userId") UUID userId,
            @Path("conversationId") UUID conversationId
    );

    @GET("api/chat/conversations/{userId}")
    Call<List<ChatMessage>> getUserConversations(@Path("userId") UUID userId);
}