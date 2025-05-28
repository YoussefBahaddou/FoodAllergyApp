package ma.emsi.foodallergyapp.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ma.emsi.foodallergyapp.databinding.FragmentHistoryBinding;
import ma.emsi.foodallergyapp.utils.SessionManager;
import ma.emsi.foodallergyapp.utils.SupabaseClientHelper;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;
    private HistoryAdapter adapter;
    private List<HistoryItem> historyList;
    private SessionManager sessionManager;
    private SupabaseClientHelper supabaseClient;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sessionManager = new SessionManager(getContext());
        supabaseClient = SupabaseClientHelper.getInstance();

        setupRecyclerView();
        loadHistoryData();
        return root;
    }

    private void setupRecyclerView() {
        historyList = new ArrayList<>();
        adapter = new HistoryAdapter(historyList);
        binding.recyclerHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerHistory.setAdapter(adapter);
    }

    private void loadHistoryData() {
        String userId = sessionManager.getUserId();
        if (userId == null) {
            showError("User not logged in");
            return;
        }

        // Show loading state
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.recyclerHistory.setVisibility(View.GONE);
        binding.textNoHistory.setVisibility(View.GONE);

        supabaseClient.getScanHistory(userId, new SupabaseClientHelper.ScanHistoryListCallback() {
            @Override
            public void onSuccess(List<SupabaseClientHelper.ScanHistoryItem> scanHistoryItems) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        binding.progressBar.setVisibility(View.GONE);

                        historyList.clear();
                        for (SupabaseClientHelper.ScanHistoryItem scanItem : scanHistoryItems) {
                            HistoryItem historyItem = convertToHistoryItem(scanItem);
                            historyList.add(historyItem);
                        }

                        if (historyList.isEmpty()) {
                            binding.textNoHistory.setVisibility(View.VISIBLE);
                            binding.recyclerHistory.setVisibility(View.GONE);
                        } else {
                            binding.textNoHistory.setVisibility(View.GONE);
                            binding.recyclerHistory.setVisibility(View.VISIBLE);
                        }

                        adapter.notifyDataSetChanged();
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        binding.progressBar.setVisibility(View.GONE);
                        showError("Failed to load history: " + error);
                        loadMockData(); // Fallback to mock data
                    });
                }
            }
        });
    }

    private HistoryItem convertToHistoryItem(SupabaseClientHelper.ScanHistoryItem scanItem) {
        String productName = "Product: " + scanItem.getScanInput();
        String result;

        if (scanItem.isSafe()) {
            result = "Aucun allergène détecté";
        } else {
            if (scanItem.getDetectedAllergens() != null && !scanItem.getDetectedAllergens().isEmpty()) {
                result = "Contient: " + String.join(", ", scanItem.getDetectedAllergens());
            } else {
                result = "Allergènes détectés";
            }
        }

        String timestamp = formatTimestamp(scanItem.getScannedAt());

        return new HistoryItem(productName, result, timestamp, scanItem.isSafe());
    }

    private String formatTimestamp(String timestamp) {
        try {
            // Parse the timestamp and format it nicely
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            Date date = inputFormat.parse(timestamp);
            return outputFormat.format(date);
        } catch (Exception e) {
            return timestamp; // Return original if parsing fails
        }
    }

    private void loadMockData() {
        // Fallback mock data
        historyList.clear();
        historyList.add(new HistoryItem("Coca-Cola", "Aucun allergène détecté", "Aujourd'hui 14:30", true));
        historyList.add(new HistoryItem("Pain aux amandes", "Contient: Fruits à coque", "Hier 16:45", false));
        historyList.add(new HistoryItem("Yaourt nature", "Contient: Lait", "Hier 12:20", false));

        if (historyList.isEmpty()) {
            binding.textNoHistory.setVisibility(View.VISIBLE);
            binding.recyclerHistory.setVisibility(View.GONE);
        } else {
            binding.textNoHistory.setVisibility(View.GONE);
            binding.recyclerHistory.setVisibility(View.VISIBLE);
        }

        adapter.notifyDataSetChanged();
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static class HistoryItem {
        private String productName;
        private String result;
        private String timestamp;
        private boolean isSafe;

        public HistoryItem(String productName, String result, String timestamp, boolean isSafe) {
            this.productName = productName;
            this.result = result;
            this.timestamp = timestamp;
            this.isSafe = isSafe;
        }

        // Getters
        public String getProductName() { return productName; }
        public String getResult() { return result; }
        public String getTimestamp() { return timestamp; }
        public boolean isSafe() { return isSafe; }
    }
}
