package ma.emsi.foodallergyapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import ma.emsi.foodallergyapp.R;
import ma.emsi.foodallergyapp.databinding.FragmentHomeBinding;
import ma.emsi.foodallergyapp.ui.scanner.ScannerActivity;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupObservers();
        setupClickListeners();

        return root;
    }

    private void setupObservers() {
        homeViewModel.getText().observe(getViewLifecycleOwner(), text -> {
            if (binding.textHome != null) {
                binding.textHome.setText(text);
            }
        });
    }

    private void setupClickListeners() {
        // Add click listeners for home screen buttons if they exist
        // This is where you would add navigation to scanner, allergies management, etc.
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}