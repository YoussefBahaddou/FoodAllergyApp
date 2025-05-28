package ma.emsi.foodallergyapp.api;

import ma.emsi.foodallergyapp.model.Allergen;
import ma.emsi.foodallergyapp.model.ScanRequest;
import ma.emsi.foodallergyapp.model.ScanResult;
import retrofit2.Call;
import retrofit2.http.*;
import java.util.List;
import java.util.Set;

public interface ApiService {

    @GET("api/allergy/allergens")
    Call<List<Allergen>> getAllAllergens();

    @POST("api/allergy/user/{userId}/allergens")
    Call<String> updateUserAllergens(@Path("userId") String userId, @Body List<String> allergenIds);

    @GET("api/allergy/user/{userId}/allergens")
    Call<Set<Allergen>> getUserAllergens(@Path("userId") String userId);

    @POST("api/allergy/scan")
    Call<ScanResult> scanProduct(@Body ScanRequest request);
}