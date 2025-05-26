package ma.emsi.foodallergyapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class AuthManager {
    private static final String TAG = "AuthManager";
    private static final String PREF_NAME = "food_allergy_prefs";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_EMAIL = "email";

    private static AuthManager instance;
    private final SharedPreferences prefs;
    private final SupabaseClientHelper supabaseHelper;

    private AuthManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        supabaseHelper = SupabaseClientHelper.getInstance(context);
    }

    public static synchronized AuthManager getInstance(Context context) {
        if (instance == null) {
            instance = new AuthManager(context.getApplicationContext());
        }
        return instance;
    }

    public interface AuthCallback {
        void onSuccess(MockUserInfo user);
        void onError(Exception e);
    }

    public static class MockUserInfo {
        private final String id;
        private final String email;

        public MockUserInfo(String id, String email) {
            this.id = id;
            this.email = email;
        }

        public String getId() {
            return id;
        }

        public String getEmail() {
            return email;
        }
    }

    public void signUp(String email, String password, String username, final AuthCallback callback) {
        // Mock implementation for now
        Log.d(TAG, "Attempting signup for: " + email);
        try {
            // Simulate network delay
            new Thread(() -> {
                try {
                    Thread.sleep(1000); // Simulate network delay
                    MockUserInfo user = new MockUserInfo("user_" + System.currentTimeMillis(), email);
                    saveUserSession(user);

                    // Run callback on main thread
                    android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                    mainHandler.post(() -> callback.onSuccess(user));
                } catch (InterruptedException e) {
                    android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                    mainHandler.post(() -> callback.onError(e));
                }
            }).start();
        } catch (Exception e) {
            Log.e(TAG, "Error during signup: " + e.getMessage());
            callback.onError(e);
        }
    }

    public void signIn(String email, String password, final AuthCallback callback) {
        // Mock implementation for now
        Log.d(TAG, "Attempting signin for: " + email);
        try {
            // Simulate network delay
            new Thread(() -> {
                try {
                    Thread.sleep(1000); // Simulate network delay
                    MockUserInfo user = new MockUserInfo("user_" + System.currentTimeMillis(), email);
                    saveUserSession(user);

                    // Run callback on main thread
                    android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                    mainHandler.post(() -> callback.onSuccess(user));
                } catch (InterruptedException e) {
                    android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                    mainHandler.post(() -> callback.onError(e));
                }
            }).start();
        } catch (Exception e) {
            Log.e(TAG, "Error during signin: " + e.getMessage());
            callback.onError(e);
        }
    }

    public void signOut() {
        Log.d(TAG, "Signing out user");
        clearUserSession();
    }

    private void saveUserSession(MockUserInfo user) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_USER_ID, user.getId());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_TOKEN, "token_saved");
        editor.apply();
        Log.d(TAG, "User session saved");
    }

    private void clearUserSession() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_TOKEN);
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_EMAIL);
        editor.apply();
        Log.d(TAG, "User session cleared");
    }

    public boolean isAuthenticated() {
        return prefs.contains(KEY_TOKEN) && !prefs.getString(KEY_TOKEN, "").isEmpty();
    }

    public String getUserId() {
        return prefs.getString(KEY_USER_ID, "");
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }
}
