package ma.emsi.foodallergyapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import java.util.UUID;

public class SessionManager {
    private static final String PREF_NAME = "FoodAllergySession";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_ALLERGIES_SELECTED = "allergies_selected";
    private static final String KEY_SESSION_TOKEN = "session_token";
    private static final String KEY_LOGIN_TIME = "login_time";

    private static final long SESSION_TIMEOUT = 24 * 60 * 60 * 1000; // 24 hours

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        try {
            // Create encrypted shared preferences
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            // Fallback to regular SharedPreferences if encryption fails
            sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
        editor = sharedPreferences.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(String userId, String email, String name) {
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_NAME, name);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putLong(KEY_LOGIN_TIME, System.currentTimeMillis());
        editor.apply();
    }

    /**
     * Create login session with UUID
     */
    public void createLoginSession(UUID userId, String email, String name) {
        createLoginSession(userId.toString(), email, name);
    }

    /**
     * Get user ID as String
     */
    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    /**
     * Get user ID as UUID
     */
    public UUID getUserIdAsUUID() {
        String userIdString = getUserId();
        if (userIdString != null) {
            try {
                return UUID.fromString(userIdString);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Get user email
     */
    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, null);
    }

    /**
     * Get user name
     */
    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, null);
    }

    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Check if session is valid (not expired)
     */
    public boolean isSessionValid() {
        if (!isLoggedIn()) {
            return false;
        }

        long loginTime = sharedPreferences.getLong(KEY_LOGIN_TIME, 0);
        long currentTime = System.currentTimeMillis();

        return (currentTime - loginTime) < SESSION_TIMEOUT;
    }

    /**
     * Set allergies selection status
     */
    public void setAllergiesSelected(boolean selected) {
        editor.putBoolean(KEY_ALLERGIES_SELECTED, selected);
        editor.apply();
    }

    /**
     * Check if allergies are selected
     */
    public boolean areAllergiesSelected() {
        return sharedPreferences.getBoolean(KEY_ALLERGIES_SELECTED, false);
    }

    /**
     * Set session token
     */
    public void setSessionToken(String token) {
        editor.putString(KEY_SESSION_TOKEN, token);
        editor.apply();
    }

    /**
     * Get session token
     */
    public String getSessionToken() {
        return sharedPreferences.getString(KEY_SESSION_TOKEN, null);
    }

    /**
     * Update user profile
     */
    public void updateUserProfile(String email, String name) {
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_NAME, name);
        editor.apply();
    }

    /**
     * Clear session data
     */
    public void logoutUser() {
        editor.clear();
        editor.apply();
    }

    /**
     * Check if this is first time login
     */
    public boolean isFirstTimeLogin() {
        return !sharedPreferences.contains(KEY_USER_ID);
    }

    /**
     * Refresh session (extend timeout)
     */
    public void refreshSession() {
        if (isLoggedIn()) {
            editor.putLong(KEY_LOGIN_TIME, System.currentTimeMillis());
            editor.apply();
        }
    }
}
