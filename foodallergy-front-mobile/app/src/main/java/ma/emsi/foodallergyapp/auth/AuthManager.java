package ma.emsi.foodallergyapp.auth;

import android.content.Context;
import android.content.SharedPreferences;
import ma.emsi.foodallergyapp.utils.SupabaseClientHelper;

public class AuthManager {
    private static final String PREFS_NAME = "auth_prefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_ALLERGIES_SELECTED = "allergies_selected";

    private SharedPreferences prefs;
    private SupabaseClientHelper supabaseClient;

    // Add static instance for singleton pattern
    private static AuthManager instance;

    public AuthManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        supabaseClient = SupabaseClientHelper.getInstance();
    }

    // Add getInstance method for singleton pattern
    public static synchronized AuthManager getInstance(Context context) {
        if (instance == null) {
            instance = new AuthManager(context.getApplicationContext());
        }
        return instance;
    }

    // Callback interfaces
    public interface LoginCallback {
        void onSuccess(MockUserInfo user);
        void onError(String error);
    }

    public interface SignupCallback {
        void onSuccess(MockUserInfo user);
        void onError(String error);
    }

    public interface UpdateCallback {
        void onSuccess(MockUserInfo user);
        void onError(String error);
    }

    // User info class
    public static class MockUserInfo {
        private String id;
        private String email;
        private String name;
        private boolean allergiesSelected;

        public MockUserInfo(String id, String email, String name, boolean allergiesSelected) {
            this.id = id;
            this.email = email;
            this.name = name;
            this.allergiesSelected = allergiesSelected;
        }

        public String getId() { return id; }
        public String getEmail() { return email; }
        public String getName() { return name; }
        public boolean isAllergiesSelected() { return allergiesSelected; }

        public void setName(String name) { this.name = name; }
        public void setEmail(String email) { this.email = email; }
        public void setAllergiesSelected(boolean allergiesSelected) { this.allergiesSelected = allergiesSelected; }
    }

    // Login method
    public void login(String email, String password, LoginCallback callback) {
        supabaseClient.signIn(email, password, new SupabaseClientHelper.AuthCallback() {
            @Override
            public void onSuccess(String userId, String userEmail, String userName) {
                // Save user session
                saveUserSession(userId, userEmail, userName, false); // Default allergies not selected

                MockUserInfo user = new MockUserInfo(userId, userEmail, userName, false);
                callback.onSuccess(user);
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }

    // Signup method
    public void signup(String name, String email, String password, SignupCallback callback) {
        supabaseClient.signUp(email, password, name, new SupabaseClientHelper.AuthCallback() {
            @Override
            public void onSuccess(String userId, String userEmail, String userName) {
                // Save user session
                saveUserSession(userId, userEmail, userName, false);

                MockUserInfo user = new MockUserInfo(userId, userEmail, userName, false);
                callback.onSuccess(user);
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }

    // Update user profile
    public void updateUserProfile(String name, String email, UpdateCallback callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.onError("User not logged in");
            return;
        }

        supabaseClient.updateUserProfile(userId, name, email, new SupabaseClientHelper.AuthCallback() {
            @Override
            public void onSuccess(String userId, String userEmail, String userName) {
                // Update local session
                boolean allergiesSelected = prefs.getBoolean(KEY_ALLERGIES_SELECTED, false);
                saveUserSession(userId, userEmail, userName, allergiesSelected);

                MockUserInfo user = new MockUserInfo(userId, userEmail, userName, allergiesSelected);
                callback.onSuccess(user);
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }

    // Save user session locally
    private void saveUserSession(String userId, String email, String name, boolean allergiesSelected) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_NAME, name);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putBoolean(KEY_ALLERGIES_SELECTED, allergiesSelected);
        editor.apply();
    }

    // Update allergies selection status
    public void updateAllergiesSelected(boolean selected) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_ALLERGIES_SELECTED, selected);
        editor.apply();
    }

    // Check if user is logged in
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Get current user info
    public MockUserInfo getCurrentUser() {
        if (!isLoggedIn()) {
            return null;
        }

        String id = prefs.getString(KEY_USER_ID, null);
        String email = prefs.getString(KEY_USER_EMAIL, null);
        String name = prefs.getString(KEY_USER_NAME, null);
        boolean allergiesSelected = prefs.getBoolean(KEY_ALLERGIES_SELECTED, false);

        if (id != null && email != null && name != null) {
            return new MockUserInfo(id, email, name, allergiesSelected);
        }

        return null;
    }

    // Get current user ID
    public String getCurrentUserId() {
        return prefs.getString(KEY_USER_ID, null);
    }

    // Get current user name
    public String getCurrentUserName() {
        return prefs.getString(KEY_USER_NAME, null);
    }

    // Get current user email
    public String getCurrentUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, null);
    }

    // Check if allergies are selected
    public boolean areAllergiesSelected() {
        return prefs.getBoolean(KEY_ALLERGIES_SELECTED, false);
    }

    // Logout
    public void logout() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    // Cleanup method (for compatibility)
    public void cleanup() {
        // This method can be used for any cleanup operations if needed
        // Currently not needed but keeping for backward compatibility
    }
}
