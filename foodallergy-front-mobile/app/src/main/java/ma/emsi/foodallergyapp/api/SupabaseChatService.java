package ma.emsi.foodallergyapp.api;

import retrofit2.Call;
import retrofit2.http.*;
import ma.emsi.foodallergyapp.model.ChatRequest;
import ma.emsi.foodallergyapp.model.ChatResponse;

public interface SupabaseChatService {

    @POST("functions/v1/chat-ai")
    @Headers({
        "apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJzbXdkdml1eWpyYmd6dGZ0emxnIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDgyNjQ3MjgsImV4cCI6MjA2Mzg0MDcyOH0.L5to81kRXdZ4_Zhx_MXoo7BDabXt4CPBE-Csyb7iSUM",
        "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJzbXdkdml1eWpyYmd6dGZ0emxnIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDgyNjQ3MjgsImV4cCI6MjA2Mzg0MDcyOH0.L5to81kRXdZ4_Zhx_MXoo7BDabXt4CPBE-Csyb7iSUM",
        "Content-Type: application/json"
    })
    Call<ChatResponse> sendMessage(@Body ChatRequest request);
}