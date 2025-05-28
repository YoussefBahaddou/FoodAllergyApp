package ma.emsi.foodallergyapp.ui.gallery;

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
import ma.emsi.foodallergyapp.databinding.FragmentGalleryBinding;
import ma.emsi.foodallergyapp.ui.scanner.ScannerActivity;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private GalleryViewModel galleryViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel = new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupObservers();
        setupClickListeners();

        return root;
    }

    private void setupObservers() {
        galleryViewModel.getText().observe(getViewLifecycleOwner(), text -> {
            if (binding.textGallery != null) {
                binding.textGallery.setText(text);
            }
        });

        galleryViewModel.getIsScanning().observe(getViewLifecycleOwner(), isScanning -> {
            // Update UI based on scanning state
            if (binding.btnStartScanner != null) {
                binding.btnStartScanner.setEnabled(!isScanning);
                binding.btnStartScanner.setText(isScanning ? "Scanning..." : getString(R.string.btn_scan_product));
            }
        });

        galleryViewModel.getScanResult().observe(getViewLifecycleOwner(), result -> {
            // Handle scan result
            if (result != null && !result.isEmpty()) {
                // Process the scan result
                processScanResult(result);
            }
        });
    }

    private void setupClickListeners() {
        if (binding.btnStartScanner != null) {
            binding.btnStartScanner.setOnClickListener(v -> startScanner());
        }

        if (binding.btnManualEntry != null) {
            binding.btnManualEntry.setOnClickListener(v -> showManualEntryDialog());
        }
    }

    private void startScanner() {
        Intent intent = new Intent(getActivity(), ScannerActivity.class);
        startActivity(intent);
    }

    private void showManualEntryDialog() {
        // TODO: Implement manual barcode entry dialog
        if (getContext() != null) {
            android.widget.Toast.makeText(getContext(), "Saisie manuelle à venir", android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    private void processScanResult(String result) {
        // TODO: Process the scanned barcode result
        if (getContext() != null) {
            android.widget.Toast.makeText(getContext(), "Code scanné: " + result, android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}