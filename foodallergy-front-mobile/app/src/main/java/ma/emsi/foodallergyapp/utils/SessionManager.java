package ma.emsi.foodallergyapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SessionManager {
    private static final String PREF_NAME = "FoodAllergySession";

    // Add the missing KEY_USER_ID constant
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_ALLERGIES_SELECTED = "allergiesSelected";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void createSession(String userId, String email, String name) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_NAME, name);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUserId() {
        return prefs.getString(KEY_USER_ID, null);
    }

    public String getUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, null);
    }

    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, null);
    }

    public void setAllergiesSelected(boolean selected) {
        editor.putBoolean(KEY_ALLERGIES_SELECTED, selected);
        editor.apply();
    }

    public boolean areAllergiesSelected() {
        return prefs.getBoolean(KEY_ALLERGIES_SELECTED, false);
    }

    public void logout() {
        editor.clear();
        editor.apply();
        Log.d("SessionManager", "Session cleared");
    }
    // Add method to update user profile in session
    public void updateUserProfile(String email, String name) {
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_NAME, name);
        editor.apply();
    }

    // Add method to check if session is valid
    public boolean isSessionValid() {
        boolean isLoggedIn = isLoggedIn();
        String userId = getUserId();
        boolean valid = isLoggedIn && userId != null && !userId.isEmpty();

        Log.d("SessionManager", "Session validation - IsLoggedIn: " + isLoggedIn +
              ", UserId: " + userId + ", Valid: " + valid);

        return valid;
    }
}
