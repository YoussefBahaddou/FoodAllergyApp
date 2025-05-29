package ma.emsi.foodallergyapp.repository;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import ma.emsi.foodallergyapp.model.ScanHistory;

public class HistoryRepository {

    private static final String TAG = "HistoryRepository";

    public interface HistoryCallback {
        void onSuccess(List<ScanHistory> historyList);
        void onError(String errorMessage);
    }

    public void getUserScanHistory(String userId, HistoryCallback callback) {
        // TODO: Replace with actual Supabase API call
        // For now, return mock data

        try {
            // Simulate network delay
            new Thread(() -> {
                try {
                    Thread.sleep(1000); // Simulate loading

                    // Mock data for testing
                    List<ScanHistory> mockHistory = createMockHistory(userId);

                    // Return on main thread
                    android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                    mainHandler.post(() -> callback.onSuccess(mockHistory));

                } catch (InterruptedException e) {
                    android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                    mainHandler.post(() -> callback.onError("Failed to load history"));
                }
            }).start();

        } catch (Exception e) {
            Log.e(TAG, "Error loading scan history", e);
            callback.onError("Failed to load scan history: " + e.getMessage());
        }
    }

    private List<ScanHistory> createMockHistory(String userId) {
        List<ScanHistory> mockHistory = new ArrayList<>();

        // Mock scan history items
        ScanHistory history1 = new ScanHistory();
        history1.setId(UUID.randomUUID());
        history1.setUserId(UUID.fromString(userId));
        history1.setScanType("barcode");
        history1.setScanInput("1234567890123");
        history1.setProductName("Pain de mie complet");
        history1.setSafe(false);
        history1.setDetectedAllergens(Arrays.asList("Gluten"));
        history1.setScannedAt(new Date(System.currentTimeMillis() - 3600000)); // 1 hour ago

        ScanHistory history2 = new ScanHistory();
        history2.setId(UUID.randomUUID());
        history2.setUserId(UUID.fromString(userId));
        history2.setScanType("barcode");
        history2.setScanInput("2345678901234");
        history2.setProductName("Yaourt nature");
        history2.setSafe(true);
        history2.setDetectedAllergens(new ArrayList<>());
        history2.setScannedAt(new Date(System.currentTimeMillis() - 7200000)); // 2 hours ago

        ScanHistory history3 = new ScanHistory();
        history3.setId(UUID.randomUUID());
        history3.setUserId(UUID.fromString(userId));
        history3.setScanType("manual");
        history3.setScanInput("chocolate, peanuts, milk");
        history3.setProductName("Chocolate Bar");
        history3.setSafe(false);
        history3.setDetectedAllergens(Arrays.asList("Arachides", "Lait"));
        history3.setScannedAt(new Date(System.currentTimeMillis() - 86400000)); // 1 day ago

        mockHistory.add(history1);
        mockHistory.add(history2);
        mockHistory.add(history3);

        return mockHistory;
    }

    public void addScanToHistory(ScanHistory scanHistory, HistoryCallback callback) {
        // TODO: Implement actual Supabase insert
        // For now, just simulate success
        new Thread(() -> {
            try {
                Thread.sleep(500); // Simulate network delay
                android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                mainHandler.post(() -> callback.onSuccess(new ArrayList<>()));
            } catch (InterruptedException e) {
                android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                mainHandler.post(() -> callback.onError("Failed to save scan history"));
            }
        }).start();
    }
}