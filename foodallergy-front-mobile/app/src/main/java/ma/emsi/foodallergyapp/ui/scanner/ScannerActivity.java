package ma.emsi.foodallergyapp.ui.scanner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import ma.emsi.foodallergyapp.R;
import ma.emsi.foodallergyapp.databinding.ActivityScannerBinding;

public class ScannerActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private ActivityScannerBinding binding;
    private boolean isFlashOn = false;
    private boolean isCameraInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScannerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupClickListeners();
        checkCameraPermission();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.scanner_title));
        }
    }

    private void setupClickListeners() {
        binding.btnManualSearch.setOnClickListener(v -> performManualSearch());
        binding.btnToggleFlash.setOnClickListener(v -> toggleFlash());
    }

    private void performManualSearch() {
        String barcode = binding.etManualBarcode.getText().toString().trim();

        if (barcode.isEmpty()) {
            binding.tilManualBarcode.setError(getString(R.string.please_enter_barcode));
            return;
        }

        binding.tilManualBarcode.setError(null);
        searchProduct(barcode);
    }

    private void searchProduct(String barcode) {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnManualSearch.setEnabled(false);

        // Simulate API call
        binding.progressBar.postDelayed(() -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnManualSearch.setEnabled(true);

            // Mock result
            String message = getString(R.string.product_found_for_barcode, barcode);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();

            // TODO: Navigate to product details or show results

        }, 2000);
    }

    private void toggleFlash() {
        if (!isCameraInitialized) {
            Toast.makeText(this, getString(R.string.camera_not_initialized), Toast.LENGTH_SHORT).show();
            return;
        }

        isFlashOn = !isFlashOn;

        // Use string variable to avoid ambiguity
        String flashText = isFlashOn ? getString(R.string.flash_on) : getString(R.string.flash_off);
        binding.btnToggleFlash.setText(flashText);

        // TODO: Implement actual flash toggle when camera is implemented
        String message = isFlashOn ? getString(R.string.flash_turned_on) : getString(R.string.flash_turned_off);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            initializeCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeCamera();
            } else {
                Toast.makeText(this, getString(R.string.camera_permission_required), Toast.LENGTH_LONG).show();
                // Hide camera preview and show only manual input
                binding.cameraPreviewContainer.setVisibility(View.GONE);
            }
        }
    }

    private void initializeCamera() {
        // TODO: Initialize camera preview here
        // For now, just mark as initialized
        isCameraInitialized = true;

        // Show placeholder message
        Toast.makeText(this, getString(R.string.camera_preview_placeholder), Toast.LENGTH_SHORT).show();

        // You can add a placeholder view or text in the camera container
        binding.tvScanInstructions.setText(getString(R.string.camera_not_implemented));

        // Initialize flash button text
        String flashOffText = getString(R.string.flash_off);
        binding.btnToggleFlash.setText(flashOffText);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TODO: Clean up camera resources when implemented
        binding = null;
    }

    // Helper method to handle barcode detection (for future camera implementation)
    private void onBarcodeDetected(String barcode) {
        runOnUiThread(() -> {
            binding.etManualBarcode.setText(barcode);
            searchProduct(barcode);
        });
    }

    // Helper method to show error messages
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    // Helper method to show success messages
    private void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
