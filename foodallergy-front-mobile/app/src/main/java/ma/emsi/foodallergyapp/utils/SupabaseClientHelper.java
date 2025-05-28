package ma.emsi.foodallergyapp.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class SupabaseClientHelper {

    private static final String TAG = "SupabaseClient";
    private static final String CONFIG_FILE = "supabase_config.properties";
    private static final String DEFAULT_URL = "https://bsmwdviuyjrbgztftzlg.supabase.co";
    private static final String DEFAULT_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJzbXdkdml1eWpyYmd6dGZ0emxnIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDgyNjQ3MjgsImV4cCI6MjA2Mzg0MDcyOH0.L5to81kRXdZ4_Zhx_MXoo7BDabXt4CPBE-Csyb7iSUM";

    private static String supabaseUrl = DEFAULT_URL;
    private static String supabaseKey = DEFAULT_KEY;
    private static SupabaseClientHelper instance;

    public interface SupabaseCallback {
        void onSuccess(String response);
        void onError(Exception error);
    }

    public static SupabaseClientHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (SupabaseClientHelper.class) {
                if (instance == null) {
                    instance = new SupabaseClientHelper();
                    instance.loadCredentials(context);
                    Log.d(TAG, "Supabase client helper initialized successfully");
                }
            }
        }
        return instance;
    }

    private void loadCredentials(Context context) {
        Properties properties = new Properties();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = null;

        try {
            inputStream = assetManager.open(CONFIG_FILE);
            properties.load(inputStream);

            String url = properties.getProperty("SUPABASE_URL");
            String key = properties.getProperty("SUPABASE_KEY");

            if (url != null && !url.trim().isEmpty() && key != null && !key.trim().isEmpty()) {
                supabaseUrl = url.trim();
                supabaseKey = key.trim();
                Log.d(TAG, "Supabase credentials loaded from config file");
            } else {
                Log.w(TAG, "Supabase credentials not properly defined in config file. Using defaults.");
            }

        } catch (IOException e) {
            Log.e(TAG, "Error loading Supabase config: " + e.getMessage());
            Log.i(TAG, "Using default Supabase credentials");
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing input stream: " + e.getMessage());
                }
            }
        }
    }

    public void getAllergens(SupabaseCallback callback) {
        // Mock implementation - simulate API call
        new Thread(() -> {
            try {
                Thread.sleep(1000); // Simulate network delay
                android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                mainHandler.post(() -> callback.onSuccess("[]")); // Empty JSON array
            } catch (InterruptedException e) {
                android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                mainHandler.post(() -> callback.onError(e));
            }
        }).start();
    }

    public void saveUserAllergies(String userId, List<String> allergenIds, SupabaseCallback callback) {
        // Mock implementation - simulate API call
        new Thread(() -> {
            try {
                Thread.sleep(1000); // Simulate network delay
                Log.d(TAG, "Saving allergies for user: " + userId + ", allergies: " + allergenIds.toString());
                android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                mainHandler.post(() -> callback.onSuccess("success"));
            } catch (InterruptedException e) {
                android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                mainHandler.post(() -> callback.onError(e));
            }
        }).start();
    }

    // Method to get current URL
    public String getUrl() {
        return supabaseUrl;
    }

    // Method to get current key
    public String getKey() {
        return supabaseKey;
    }

    // Method to get current URL (for debugging)
    public static String getCurrentUrl() {
        return supabaseUrl;
    }

    // Method to get current key (for debugging - be careful with this in production)
    public static String getCurrentKey() {
        return supabaseKey.substring(0, 10) + "..."; // Only show first 10 characters for security
    }

    private SupabaseClientHelper() {
        // Private constructor to prevent instantiation
    }
}
