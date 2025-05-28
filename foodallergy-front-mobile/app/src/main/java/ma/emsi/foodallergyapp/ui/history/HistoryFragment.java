package ma.emsi.foodallergyapp.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import ma.emsi.foodallergyapp.databinding.FragmentHistoryBinding;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;
    private HistoryAdapter adapter;
    private List<HistoryItem> historyList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

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
        // Mock data for now
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