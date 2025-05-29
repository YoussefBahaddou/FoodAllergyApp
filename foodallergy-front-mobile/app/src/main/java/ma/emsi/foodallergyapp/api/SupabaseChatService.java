package ma.emsi.foodallergyapp.api;

import retrofit2.Call;
import retrofit2.http.*;
import ma.emsi.foodallergyapp.model.ChatRequest;
import ma.emsi.foodallergyapp.model.ChatResponse;

public interface SupabaseChatService {

    @POST("functions/v1/chat-ai")
    @Headers({
        "apikey: YOUR_SUPABASE_ANON_KEY",
        "Authorization: Bearer YOUR_SUPABASE_ANON_KEY"
    })
    Call<ChatResponse> sendMessage(@Body ChatRequest request);
}