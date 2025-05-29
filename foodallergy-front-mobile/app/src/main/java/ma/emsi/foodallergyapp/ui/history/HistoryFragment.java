package ma.emsi.foodallergyapp.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.UUID;

import ma.emsi.foodallergyapp.databinding.FragmentHistoryBinding;
import ma.emsi.foodallergyapp.model.ScanHistory;
import ma.emsi.foodallergyapp.utils.SessionManager;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;
    private HistoryViewModel historyViewModel;
    private SessionManager sessionManager;
    private HistoryAdapter historyAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        historyViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);

        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize session manager
        sessionManager = new SessionManager(requireContext());

        setupRecyclerView();
        setupObservers();
        loadScanHistory();

        return root;
    }

    private void setupRecyclerView() {
        historyAdapter = new HistoryAdapter(new ArrayList<>());
        binding.recyclerHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerHistory.setAdapter(historyAdapter);
    }

    private void setupObservers() {
        historyViewModel.getScanHistory().observe(getViewLifecycleOwner(), scanHistoryList -> {
            if (scanHistoryList != null && !scanHistoryList.isEmpty()) {
                binding.recyclerHistory.setVisibility(View.VISIBLE);
                binding.textNoHistory.setVisibility(View.GONE);
                historyAdapter.updateHistory(scanHistoryList);
            } else {
                binding.recyclerHistory.setVisibility(View.GONE);
                binding.textNoHistory.setVisibility(View.VISIBLE);
            }
        });

        historyViewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        historyViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadScanHistory() {
        // Get user ID as UUID, then convert to String for API call
        UUID userIdUUID = sessionManager.getUserIdAsUUID();
        if (userIdUUID != null) {
            String userId = userIdUUID.toString();
            historyViewModel.loadScanHistory(userId);
        } else {
            // Handle case where user ID is not available
            String userId = sessionManager.getUserId(); // Get as String directly
            if (userId != null) {
                historyViewModel.loadScanHistory(userId);
            } else {
                Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
