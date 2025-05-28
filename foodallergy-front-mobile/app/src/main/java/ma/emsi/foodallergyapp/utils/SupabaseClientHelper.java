package ma.emsi.foodallergyapp.utils;

import android.util.Log;
import ma.emsi.foodallergyapp.model.Allergen;
import ma.emsi.foodallergyapp.ui.allergies.AllergySelectionActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.*;

public class SupabaseClientHelper {
    private static final String TAG = "SupabaseClientHelper";
    private static final String SUPABASE_URL = "https://bsmwdviuyjrbgztftzlg.supabase.co";
    private static final String SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJzbXdkdml1eWpyYmd6dGZ0emxnIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDgyNjQ3MjgsImV4cCI6MjA2Mzg0MDcyOH0.L5to81kRXdZ4_Zhx_MXoo7BDabXt4CPBE-Csyb7iSUM";

    private OkHttpClient client;
    private static SupabaseClientHelper instance;

    public SupabaseClientHelper() {
        this.client = new OkHttpClient();
    }

    // Singleton pattern
    public static synchronized SupabaseClientHelper getInstance() {
        if (instance == null) {
            instance = new SupabaseClientHelper();
        }
        return instance;
    }

    // Authentication callback interface
    public interface AuthCallback {
        void onSuccess(String userId, String email, String name);
        void onError(String error);
    }

    // Callback interface for allergens
    public interface AllergenCallback {
        void onSuccess(List<AllergySelectionActivity.Allergen> allergens);
        void onError(String error);
    }

    // Callback interface for saving allergens
    public interface SaveCallback {
        void onSuccess();
        void onError(String error);
    }

    // Sign up new user
    public void signUp(String email, String password, String username, AuthCallback callback) {
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("username", username);
            jsonBody.put("email", email);
            jsonBody.put("password", password);
            jsonBody.put("allergies_selected", false);

            RequestBody body = RequestBody.create(
                    jsonBody.toString(),
                    MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(SUPABASE_URL + "/rest/v1/users")
                    .post(body)
                    .addHeader("apikey", SUPABASE_ANON_KEY)
                    .addHeader("Authorization", "Bearer " + SUPABASE_ANON_KEY)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Prefer", "return=representation")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Database connection failed during signup", e);
                    callback.onError("Unable to connect to database");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String responseBody = response.body().string();

                        if (response.isSuccessful()) {
                            try {
                                JSONArray jsonArray = new JSONArray(responseBody);
                                if (jsonArray.length() > 0) {
                                    JSONObject user = jsonArray.getJSONObject(0);
                                    String userId = user.getString("id");
                                    String userEmail = user.getString("email");
                                    String userName = user.getString("username");
                                    callback.onSuccess(userId, userEmail, userName);
                                } else {
                                    Log.e(TAG, "Database returned empty response for signup");
                                    callback.onError("Account creation failed");
                                }
                            } catch (JSONException e) {
                                Log.e(TAG, "Database response parsing error during signup: " + responseBody, e);
                                callback.onError("Account creation failed");
                            }
                        } else {
                            Log.e(TAG, "Database error during signup - Status: " + response.code() + ", Response: " + responseBody);

                            if (response.code() == 409) {
                                callback.onError("Email already exists");
                            } else {
                                callback.onError("Account creation failed");
                            }
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error processing signup response from database", e);
                        callback.onError("Account creation failed");
                    } finally {
                        response.close();
                    }
                }
            });

        } catch (JSONException e) {
            Log.e(TAG, "Error creating signup request", e);
            callback.onError("Account creation failed");
        }
    }

    public void testConnection() {
        try {
            // Test your Supabase connection here
            Log.d(TAG, "Testing Supabase connection...");
            // Add your connection test logic
        } catch (Exception e) {
            Log.e(TAG, "Database connection failed during signup", e);
        }
    }

    // Sign in existing user
    public void signIn(String email, String password, AuthCallback callback) {
        String url = SUPABASE_URL + "/rest/v1/users?email=eq." + email + "&password=eq." + password;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("apikey", SUPABASE_ANON_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_ANON_KEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Database connection failed during signin", e);
                callback.onError("Unable to connect to database");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseBody = response.body().string();

                    if (response.isSuccessful()) {
                        try {
                            JSONArray jsonArray = new JSONArray(responseBody);
                            if (jsonArray.length() > 0) {
                                JSONObject user = jsonArray.getJSONObject(0);
                                String userId = user.getString("id");
                                String userEmail = user.getString("email");
                                String userName = user.getString("username");
                                callback.onSuccess(userId, userEmail, userName);
                            } else {
                                callback.onError("Invalid email or password");
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Database response parsing error during signin: " + responseBody, e);
                            callback.onError("Login failed");
                        }
                    } else {
                        Log.e(TAG, "Database error during signin - Status: " + response.code() + ", Response: " + responseBody);
                        callback.onError("Login failed");
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error processing signin response from database", e);
                    callback.onError("Login failed");
                } finally {
                    response.close();
                }
            }
        });
    }

    // Update user profile
    public void updateUserProfile(String userId, String username, String email, AuthCallback callback) {
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("username", username);
            jsonBody.put("email", email);

            RequestBody body = RequestBody.create(
                    jsonBody.toString(),
                    MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(SUPABASE_URL + "/rest/v1/users?id=eq." + userId)
                    .patch(body)
                    .addHeader("apikey", SUPABASE_ANON_KEY)
                    .addHeader("Authorization", "Bearer " + SUPABASE_ANON_KEY)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Prefer", "return=representation")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Database connection failed during profile update", e);
                    callback.onError("Unable to connect to database");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String responseBody = response.body().string();

                        if (response.isSuccessful()) {
                            try {
                                JSONArray jsonArray = new JSONArray(responseBody);
                                if (jsonArray.length() > 0) {
                                    JSONObject user = jsonArray.getJSONObject(0);
                                    String userEmail = user.getString("email");
                                    String userName = user.getString("username");
                                    callback.onSuccess(userId, userEmail, userName);
                                } else {
                                    Log.e(TAG, "Database returned empty response for profile update");
                                    callback.onError("Profile update failed");
                                }
                            } catch (JSONException e) {
                                Log.e(TAG, "Database response parsing error during profile update: " + responseBody, e);
                                callback.onError("Profile update failed");
                            }
                        } else {
                            Log.e(TAG, "Database error during profile update - Status: " + response.code() + ", Response: " + responseBody);
                            callback.onError("Profile update failed");
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error processing profile update response from database", e);
                        callback.onError("Profile update failed");
                    } finally {
                        response.close();
                    }
                }
            });

        } catch (JSONException e) {
            Log.e(TAG, "Error creating profile update request", e);
            callback.onError("Profile update failed");
        }
    }

    // Update user allergies selection status
    public void updateAllergiesSelected(String userId, boolean allergiesSelected, SaveCallback callback) {
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("allergies_selected", allergiesSelected);

            RequestBody body = RequestBody.create(
                    jsonBody.toString(),
                    MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(SUPABASE_URL + "/rest/v1/users?id=eq." + userId)
                    .patch(body)
                    .addHeader("apikey", SUPABASE_ANON_KEY)
                    .addHeader("Authorization", "Bearer " + SUPABASE_ANON_KEY)
                    .addHeader("Content-Type", "application/json")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Database connection failed during allergies status update", e);
                    callback.onError("Unable to connect to database");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        if (response.isSuccessful()) {
                            callback.onSuccess();
                        } else {
                            String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                            Log.e(TAG, "Database error updating allergies status - Status: " + response.code() + ", Response: " + errorBody);
                            callback.onError("Failed to update allergies status");
                        }
                    } finally {
                        response.close();
                    }
                }
            });

        } catch (JSONException e) {
            Log.e(TAG, "Error creating allergies status update request", e);
            callback.onError("Failed to update allergies status");
        }
    }

    // Get all allergens
    public void getAllergens(AllergenCallback callback) {
        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/allergens")
                .addHeader("apikey", SUPABASE_ANON_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_ANON_KEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Database connection failed while fetching allergens", e);
                callback.onError("Unable to connect to database");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        try {
                            List<Allergen> modelAllergens = parseAllergensFromJson(responseBody);
                            List<AllergySelectionActivity.Allergen> uiAllergens = convertToUIAllergens(modelAllergens);
                            callback.onSuccess(uiAllergens);
                        } catch (JSONException e) {
                            Log.e(TAG, "Database response parsing error while fetching allergens: " + responseBody, e);
                            callback.onError("Failed to load allergens");
                        }
                    } else {
                        String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                        Log.e(TAG, "Database error fetching allergens - Status: " + response.code() + ", Response: " + errorBody);
                        callback.onError("Failed to load allergens");
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error processing allergens response from database", e);
                    callback.onError("Failed to load allergens");
                } finally {
                    response.close();
                }
            }
        });
    }

    // Get user allergens
    public void getUserAllergies(String userId, AllergenCallback callback) {
        if (userId == null || userId.isEmpty()) {
            callback.onError("User ID is required");
            return;
        }

        String url = SUPABASE_URL + "/rest/v1/user_allergens?user_id=eq." + userId + "&select=*,allergens(*)";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_ANON_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_ANON_KEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Database connection failed while fetching user allergies", e);
                callback.onError("Unable to connect to database");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        try {
                            List<Allergen> modelAllergens = parseUserAllergensFromJson(responseBody);
                            List<AllergySelectionActivity.Allergen> uiAllergens = convertToUIAllergens(modelAllergens);
                            callback.onSuccess(uiAllergens);
                        } catch (JSONException e) {
                            Log.e(TAG, "Database response parsing error while fetching user allergies: " + responseBody, e);
                            callback.onError("Failed to load user allergies");
                        }
                    } else {
                        String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                        Log.e(TAG, "Database error fetching user allergies - Status: " + response.code() + ", Response: " + errorBody);
                        callback.onError("Failed to load user allergies");
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error processing user allergies response from database", e);
                    callback.onError("Failed to load user allergies");
                } finally {
                    response.close();
                }
            }
        });
    }

    // Save user allergens
    public void saveUserAllergies(String userId, List<String> allergenIds, SaveCallback callback) {
        if (userId == null || userId.isEmpty()) {
            callback.onError("User ID is required");
            return;
        }

        // First, delete existing user allergens
        deleteUserAllergies(userId, new SaveCallback() {
            @Override
            public void onSuccess() {
                // Then insert new allergens
                insertUserAllergies(userId, allergenIds, new SaveCallback() {
                    @Override
                    public void onSuccess() {
                        // Update user's allergies_selected status
                        updateAllergiesSelected(userId, true, callback);
                    }

                    @Override
                    public void onError(String error) {
                        callback.onError(error);
                    }
                });
            }

            @Override
            public void onError(String error) {
                callback.onError("Failed to clear existing allergies: " + error);
            }
        });
    }

    // Delete user allergens
    private void deleteUserAllergies(String userId, SaveCallback callback) {
        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/user_allergens?user_id=eq." + userId)
                .delete()
                .addHeader("apikey", SUPABASE_ANON_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_ANON_KEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Database connection failed while deleting user allergens", e);
                callback.onError("Unable to connect to database");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                        Log.e(TAG, "Database error deleting user allergens - Status: " + response.code() + ", Response: " + errorBody);
                        callback.onError("Failed to delete user allergies");
                    }
                } finally {
                    response.close();
                }
            }
        });
    }

    // Insert user allergens
    private void insertUserAllergies(String userId, List<String> allergenIds, SaveCallback callback) {
        if (allergenIds.isEmpty()) {
            callback.onSuccess();
            return;
        }

        try {
            JSONArray jsonArray = new JSONArray();
            for (String allergenId : allergenIds) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("user_id", userId);
                jsonObject.put("allergen_id", allergenId);
                jsonArray.put(jsonObject);
            }

            RequestBody body = RequestBody.create(
                    jsonArray.toString(),
                    MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(SUPABASE_URL + "/rest/v1/user_allergens")
                    .post(body)
                    .addHeader("apikey", SUPABASE_ANON_KEY)
                    .addHeader("Authorization", "Bearer " + SUPABASE_ANON_KEY)
                    .addHeader("Content-Type", "application/json")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Database connection failed while inserting user allergens", e);
                    callback.onError("Unable to connect to database");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        if (response.isSuccessful()) {
                            callback.onSuccess();
                        } else {
                            String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                            Log.e(TAG, "Database error inserting user allergens - Status: " + response.code() + ", Response: " + errorBody);
                            callback.onError("Failed to save user allergies");
                        }
                    } finally {
                        response.close();
                    }
                }
            });

        } catch (JSONException e) {
            Log.e(TAG, "Error creating user allergens insert request", e);
            callback.onError("Failed to save user allergies");
        }
    }

    // Helper method to convert model Allergens to UI Allergens
    private List<AllergySelectionActivity.Allergen> convertToUIAllergens(List<Allergen> modelAllergens) {
        List<AllergySelectionActivity.Allergen> uiAllergens = new ArrayList<>();
        for (Allergen modelAllergen : modelAllergens) {
            AllergySelectionActivity.Allergen uiAllergen = new AllergySelectionActivity.Allergen(
                    modelAllergen.getId(),
                    modelAllergen.getName(),
                    modelAllergen.getDescription()
            );
            uiAllergens.add(uiAllergen);
        }
        return uiAllergens;
    }

    // Parse allergens from JSON response
    private List<Allergen> parseAllergensFromJson(String json) throws JSONException {
        List<Allergen> allergens = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(json);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Allergen allergen = new Allergen();
            allergen.setId(jsonObject.optString("id"));
            allergen.setName(jsonObject.optString("name"));
            allergen.setDescription(jsonObject.optString("description"));
            allergens.add(allergen);
        }

        return allergens;
    }

    // Parse user allergens from JSON response
    private List<Allergen> parseUserAllergensFromJson(String json) throws JSONException {
        List<Allergen> allergens = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(json);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            // Get the nested allergen object
            JSONObject allergenObject = jsonObject.optJSONObject("allergens");
            if (allergenObject != null) {
                Allergen allergen = new Allergen();
                allergen.setId(allergenObject.optString("id"));
                allergen.setName(allergenObject.optString("name"));
                allergen.setDescription(allergenObject.optString("description"));
                allergens.add(allergen);
            }
        }

        return allergens;
    }
}

