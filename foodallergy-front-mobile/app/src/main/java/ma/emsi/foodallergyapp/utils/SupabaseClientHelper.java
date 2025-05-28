package ma.emsi.foodallergyapp.utils;

import android.util.Log;
import ma.emsi.foodallergyapp.model.Allergen;
import ma.emsi.foodallergyapp.model.ScanResult;
import ma.emsi.foodallergyapp.ui.allergies.AllergySelectionActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

    // Callback interface for product scanning
    public interface ProductScanCallback {
        void onSuccess(ScanResult scanResult);
        void onError(String error);
    }

    // Callback interface for scan history
    public interface ScanHistoryCallback {
        void onSuccess();
        void onError(String error);
    }

    // Callback interface for scan history list
    public interface ScanHistoryListCallback {
        void onSuccess(List<ScanHistoryItem> historyItems);
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

    // Scan product by barcode
    // Add this method to better handle product scanning
    public void scanProductByBarcode(String userId, String barcode, ProductScanCallback callback) {
        Log.d(TAG, "Starting product scan - User ID: " + userId + ", Barcode: " + barcode);

        if (userId == null || userId.isEmpty()) {
            Log.e(TAG, "User ID is null or empty");
            callback.onError("User not logged in");
            return;
        }

        if (barcode == null || barcode.isEmpty()) {
            Log.e(TAG, "Barcode is null or empty");
            callback.onError("Invalid barcode");
            return;
        }

        // First, try to find the product in the database
        String url = SUPABASE_URL + "/rest/v1/products?barcode=eq." + barcode;
        Log.d(TAG, "Querying URL: " + url);

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("apikey", SUPABASE_ANON_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_ANON_KEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Database connection failed during product scan", e);
                callback.onError("Unable to connect to database");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseBody = response.body().string();
                    Log.d(TAG, "Product scan response - Status: " + response.code() + ", Body: " + responseBody);

                    if (response.isSuccessful()) {
                        try {
                            JSONArray jsonArray = new JSONArray(responseBody);
                            if (jsonArray.length() > 0) {
                                JSONObject product = jsonArray.getJSONObject(0);
                                Log.d(TAG, "Product found in database: " + product.optString("name"));
                                analyzeProductForUser(product, userId, callback);
                            } else {
                                Log.d(TAG, "Product not found in database for barcode: " + barcode);
                                callback.onError("Product not found");
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Database response parsing error during product scan: " + responseBody, e);
                            callback.onError("Failed to parse product data");
                        }
                    } else {
                        Log.e(TAG, "Database error during product scan - Status: " + response.code() + ", Response: " + responseBody);
                        callback.onError("Failed to scan product");
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error processing product scan response from database", e);
                    callback.onError("Failed to scan product");
                } finally {
                    response.close();
                }
            }
        });
    }

    // Analyze product for user allergies
    private void analyzeProductForUser(JSONObject product, String userId, ProductScanCallback callback) {
        try {
            // Parse product data
            ScanResult scanResult = new ScanResult();
            scanResult.setProductName(product.optString("name"));
            scanResult.setBarcode(product.optString("barcode"));
            scanResult.setBrand(product.optString("brand"));
            scanResult.setAllergenInfo(product.optString("allergen_info"));

            // Parse ingredients
            String ingredientsStr = product.optString("ingredients");
            List<String> ingredients = Arrays.asList(ingredientsStr.split(","));
            for (int i = 0; i < ingredients.size(); i++) {
                ingredients.set(i, ingredients.get(i).trim());
            }
            scanResult.setIngredients(ingredients);

            // Get user allergens and check against product
            getUserAllergies(userId, new AllergenCallback() {
                @Override
                public void onSuccess(List<AllergySelectionActivity.Allergen> userAllergens) {
                    analyzeAllergenRisk(scanResult, userAllergens, callback);
                }

                @Override
                public void onError(String error) {
                    // Continue without user allergen analysis
                    Log.w(TAG, "Could not get user allergies: " + error);
                    scanResult.setHasUserAllergens(false);
                    scanResult.setRiskLevel("LOW");
                    scanResult.setAllergens(new ArrayList<>());
                    callback.onSuccess(scanResult);
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error analyzing product", e);
            callback.onError("Failed to analyze product");
        }
    }

    // Analyze allergen risk based on user allergies
    private void analyzeAllergenRisk(ScanResult scanResult, List<AllergySelectionActivity.Allergen> userAllergens, ProductScanCallback callback) {
        List<String> detectedAllergens = new ArrayList<>();
        final boolean[] hasUserAllergens = {false};
        final String[] riskLevel = {"LOW"};

        // Get all allergens from database to check keywords
        getAllergens(new AllergenCallback() {
            @Override
            public void onSuccess(List<AllergySelectionActivity.Allergen> allAllergens) {
                // Check ingredients against allergen keywords
                for (AllergySelectionActivity.Allergen allergen : allAllergens) {
                    if (checkIngredientContainsAllergen(scanResult.getIngredients(), allergen)) {
                        detectedAllergens.add(allergen.getName());

                        // Check if user has this allergen
                        for (AllergySelectionActivity.Allergen userAllergen : userAllergens) {
                            if (userAllergen.getId().equals(allergen.getId())) {
                                hasUserAllergens[0] = true;
                                break;
                            }
                        }
                    }
                }

                // Determine risk level
                if (hasUserAllergens[0]) {
                    riskLevel[0] = detectedAllergens.size() > 2 ? "HIGH" : "MEDIUM";
                } else if (!detectedAllergens.isEmpty()) {
                    riskLevel[0] = "LOW";
                }

                scanResult.setAllergens(detectedAllergens);
                scanResult.setHasUserAllergens(hasUserAllergens[0]);
                scanResult.setRiskLevel(riskLevel[0]);

                callback.onSuccess(scanResult);
            }

            @Override
            public void onError(String error) {
                // Fallback analysis
                scanResult.setAllergens(detectedAllergens);
                scanResult.setHasUserAllergens(false);
                scanResult.setRiskLevel("LOW");
                callback.onSuccess(scanResult);
            }
        });
    }

    // Check if ingredients contain allergen keywords
    private boolean checkIngredientContainsAllergen(List<String> ingredients, AllergySelectionActivity.Allergen allergen) {
        String allergenName = allergen.getName().toLowerCase();

        for (String ingredient : ingredients) {
            String ingredientLower = ingredient.toLowerCase().trim();

            // Basic keyword matching - extend this based on your allergen keywords in database
            switch (allergenName) {
                case "gluten":
                    if (ingredientLower.contains("blé") || ingredientLower.contains("gluten") ||
                            ingredientLower.contains("orge") || ingredientLower.contains("seigle")) {
                        return true;
                    }
                    break;
                case "lait":
                    if (ingredientLower.contains("lait") || ingredientLower.contains("lactose") ||
                            ingredientLower.contains("fromage") || ingredientLower.contains("beurre")) {
                        return true;
                    }
                    break;
                case "œufs":
                case "oeufs":
                    if (ingredientLower.contains("œuf") || ingredientLower.contains("oeuf") ||
                            ingredientLower.contains("albumine")) {
                        return true;
                    }
                    break;
                case "fruits à coque":
                    if (ingredientLower.contains("amande") || ingredientLower.contains("noix") ||
                            ingredientLower.contains("noisette") || ingredientLower.contains("pistache")) {
                        return true;
                    }
                    break;
                case "arachides":
                    if (ingredientLower.contains("arachide") || ingredientLower.contains("cacahuète")) {
                        return true;
                    }
                    break;
                case "soja":
                    if (ingredientLower.contains("soja") || ingredientLower.contains("tofu")) {
                        return true;
                    }
                    break;
                case "sésame":
                    if (ingredientLower.contains("sésame") || ingredientLower.contains("tahini")) {
                        return true;
                    }
                    break;
                case "fruits de mer":
                    if (ingredientLower.contains("poisson") || ingredientLower.contains("crevette") ||
                            ingredientLower.contains("crabe") || ingredientLower.contains("moule")) {
                        return true;
                    }
                    break;
            }
        }
        return false;
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

    // Save scan to history
    public void saveScanToHistory(String userId, ScanResult scanResult, ScanHistoryCallback callback) {
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("user_id", userId);
            jsonBody.put("scan_type", "barcode");
            jsonBody.put("scan_input", scanResult.getBarcode());
            jsonBody.put("is_safe", !scanResult.isHasUserAllergens());

            // Convert detected allergens to JSON array
            JSONArray allergensArray = new JSONArray();
            if (scanResult.getAllergens() != null) {
                for (String allergen : scanResult.getAllergens()) {
                    allergensArray.put(allergen);
                }
            }
            jsonBody.put("detected_allergens", allergensArray);

            RequestBody body = RequestBody.create(
                    jsonBody.toString(),
                    MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(SUPABASE_URL + "/rest/v1/scan_history")
                    .post(body)
                    .addHeader("apikey", SUPABASE_ANON_KEY)
                    .addHeader("Authorization", "Bearer " + SUPABASE_ANON_KEY)
                    .addHeader("Content-Type", "application/json")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Database connection failed while saving scan history", e);
                    callback.onError("Unable to connect to database");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        if (response.isSuccessful()) {
                            callback.onSuccess();
                        } else {
                            String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                            Log.e(TAG, "Database error saving scan history - Status: " + response.code() + ", Response: " + errorBody);
                            callback.onError("Failed to save scan history");
                        }
                    } finally {
                        response.close();
                    }
                }
            });

        } catch (JSONException e) {
            Log.e(TAG, "Error creating scan history save request", e);
            callback.onError("Failed to save scan history");
        }
    }

    // Get scan history for user
    public void getScanHistory(String userId, ScanHistoryListCallback callback) {
        String url = SUPABASE_URL + "/rest/v1/scan_history?user_id=eq." + userId + "&order=scanned_at.desc&limit=50";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("apikey", SUPABASE_ANON_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_ANON_KEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Database connection failed while fetching scan history", e);
                callback.onError("Unable to connect to database");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseBody = response.body().string();

                    if (response.isSuccessful()) {
                        try {
                            List<ScanHistoryItem> historyItems = parseScanHistoryFromJson(responseBody);
                            callback.onSuccess(historyItems);
                        } catch (JSONException e) {
                            Log.e(TAG, "Database response parsing error while fetching scan history: " + responseBody, e);
                            callback.onError("Failed to load scan history");
                        }
                    } else {
                        String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                        Log.e(TAG, "Database error fetching scan history - Status: " + response.code() + ", Response: " + errorBody);
                        callback.onError("Failed to load scan history");
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error processing scan history response from database", e);
                    callback.onError("Failed to load scan history");
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

    // Parse scan history from JSON response
    private List<ScanHistoryItem> parseScanHistoryFromJson(String json) throws JSONException {
        List<ScanHistoryItem> historyItems = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(json);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            ScanHistoryItem item = new ScanHistoryItem();
            item.setId(jsonObject.optString("id"));
            item.setScanType(jsonObject.optString("scan_type"));
            item.setScanInput(jsonObject.optString("scan_input"));
            item.setSafe(jsonObject.optBoolean("is_safe", true));
            item.setScannedAt(jsonObject.optString("scanned_at"));

            // Parse detected allergens array
            JSONArray allergensArray = jsonObject.optJSONArray("detected_allergens");
            List<String> detectedAllergens = new ArrayList<>();
            if (allergensArray != null) {
                for (int j = 0; j < allergensArray.length(); j++) {
                    detectedAllergens.add(allergensArray.getString(j));
                }
            }
            item.setDetectedAllergens(detectedAllergens);

            historyItems.add(item);
        }

        return historyItems;
    }

    // Test connection method
    public void testConnection() {
        try {
            Log.d(TAG, "Testing Supabase connection...");
            // Simple test request to check connectivity
            Request request = new Request.Builder()
                    .url(SUPABASE_URL + "/rest/v1/allergens?limit=1")
                    .get()
                    .addHeader("apikey", SUPABASE_ANON_KEY)
                    .addHeader("Authorization", "Bearer " + SUPABASE_ANON_KEY)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Supabase connection test failed", e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "Supabase connection test successful");
                        } else {
                            Log.w(TAG, "Supabase connection test returned status: " + response.code());
                        }
                    } finally {
                        response.close();
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error during Supabase connection test", e);
        }
    }

    // Inner class for scan history items
    public static class ScanHistoryItem {
        private String id;
        private String scanType;
        private String scanInput;
        private boolean isSafe;
        private String scannedAt;
        private List<String> detectedAllergens;

        public ScanHistoryItem() {}

        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getScanType() { return scanType; }
        public void setScanType(String scanType) { this.scanType = scanType; }

        public String getScanInput() { return scanInput; }
        public void setScanInput(String scanInput) { this.scanInput = scanInput; }

        public boolean isSafe() { return isSafe; }
        public void setSafe(boolean safe) { isSafe = safe; }

        public String getScannedAt() { return scannedAt; }
        public void setScannedAt(String scannedAt) { this.scannedAt = scannedAt; }

        public List<String> getDetectedAllergens() { return detectedAllergens; }
        public void setDetectedAllergens(List<String> detectedAllergens) { this.detectedAllergens = detectedAllergens; }
    }
}
