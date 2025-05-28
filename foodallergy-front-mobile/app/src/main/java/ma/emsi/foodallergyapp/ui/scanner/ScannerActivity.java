package ma.emsi.foodallergyapp.ui.scanner;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

import ma.emsi.foodallergyapp.R;
import ma.emsi.foodallergyapp.databinding.ActivityScannerBinding;

public class ScannerActivity extends AppCompatActivity {

    private ActivityScannerBinding binding;
    private boolean isFlashOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScannerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupClickListeners();
        initializeCamera();
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.scanner_title));
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupClickListeners() {
        binding.btnManualSearch.setOnClickListener(v -> performManualSearch());
        binding.btnToggleFlash.setOnClickListener(v -> toggleFlash());
    }

    private void initializeCamera() {
        // TODO: Initialize camera for barcode scanning
        // This would typically use CameraX or Camera2 API
        Toast.makeText(this, "Caméra en cours d'initialisation...", Toast.LENGTH_SHORT).show();
    }

    private void performManualSearch() {
        String barcode = binding.etManualBarcode.getText().toString().trim();

        if (TextUtils.isEmpty(barcode)) {
            binding.tilManualBarcode.setError("Veuillez saisir un code-barres");
            return;
        }

        binding.tilManualBarcode.setError(null);
        searchProduct(barcode);
    }

    private void searchProduct(String barcode) {
        binding.progressBar.setVisibility(View.VISIBLE);

        // TODO: Implement actual product search
        // For now, simulate a search and navigate to results
        binding.progressBar.postDelayed(() -> {
            binding.progressBar.setVisibility(View.GONE);

            Intent intent = new Intent(this, ScanResultActivity.class);
            intent.putExtra("barcode", barcode);
            startActivity(intent);
        }, 2000);
    }

    private void toggleFlash() {
        isFlashOn = !isFlashOn;
        binding.btnToggleFlash.setText(isFlashOn ? "Flash ON" : "Flash OFF");

        // TODO: Implement actual flash toggle
        Toast.makeText(this, isFlashOn ? "Flash activé" : "Flash désactivé", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
