package ma.emsi.foodallergyapp.ui.scanner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.*;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import ma.emsi.foodallergyapp.R;
import ma.emsi.foodallergyapp.auth.AuthManager;
import ma.emsi.foodallergyapp.auth.LoginActivity;
import ma.emsi.foodallergyapp.databinding.ActivityScannerBinding;
import ma.emsi.foodallergyapp.model.ScanResult;
import ma.emsi.foodallergyapp.utils.SupabaseClientHelper;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScannerActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final String TAG = "ScannerActivity";

    private ActivityScannerBinding binding;
    private boolean isFlashOn = false;
    private boolean isCameraInitialized = false;
    private boolean isScanning = true;

    private ProcessCameraProvider cameraProvider;
    private Preview preview;
    private ImageAnalysis imageAnalysis;
    private Camera camera;
    private BarcodeScanner barcodeScanner;
    private ExecutorService cameraExecutor;

    private AuthManager authManager;
    private SupabaseClientHelper supabaseClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScannerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authManager = new AuthManager(this);
        supabaseClient = SupabaseClientHelper.getInstance();
        cameraExecutor = Executors.newSingleThreadExecutor();

        // Check if user is logged in (same as ProfileActivity)
        if (!authManager.isLoggedIn()) {
            navigateToLogin();
            return;
        }

        // Debug user info
        debugUserInfo();

        setupToolbar();
        setupClickListeners();
        setupBarcodeScanner();
        checkCameraPermission();
    }

    private void debugUserInfo() {
        AuthManager.MockUserInfo user = authManager.getCurrentUser();
        if (user != null) {
            Log.d(TAG, "=== SCANNER ACTIVITY USER DEBUG ===");
            Log.d(TAG, "User ID: " + user.getId());
            Log.d(TAG, "User Email: " + user.getEmail());
            Log.d(TAG, "User Name: " + user.getName());
            Log.d(TAG, "Is logged in: " + authManager.isLoggedIn());
            Log.d(TAG, "===================================");
        } else {
            Log.e(TAG, "Current user is null!");
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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

    private void setupBarcodeScanner() {
        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_EAN_13, Barcode.FORMAT_EAN_8, Barcode.FORMAT_UPC_A, Barcode.FORMAT_UPC_E)
                .build();
        barcodeScanner = BarcodeScanning.getClient(options);
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
        // Check if user is still logged in before proceeding
        if (!authManager.isLoggedIn()) {
            showError("Session expired. Please log in again.");
            navigateToLogin();
            return;
        }

        AuthManager.MockUserInfo user = authManager.getCurrentUser();
        if (user == null) {
            showError("User information not available. Please log in again.");
            navigateToLogin();
            return;
        }

        String userId = user.getId();
        Log.d(TAG, "Searching product with barcode: " + barcode + " for user: " + userId);

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnManualSearch.setEnabled(false);
        isScanning = false; // Stop camera scanning while processing

        // Search product using Supabase
        supabaseClient.scanProductByBarcode(userId, barcode, new SupabaseClientHelper.ProductScanCallback() {
            @Override
            public void onSuccess(ScanResult result) {
                runOnUiThread(() -> {
                    Log.d(TAG, "Product scan successful: " + result.getProductName());
                    navigateToResults(result);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Log.e(TAG, "Product scan failed: " + error);
                    // Create mock result for unknown products
                    createMockScanResult(barcode, userId);
                });
            }
        });
    }

    private void createMockScanResult(String barcode, String userId) {
        ScanResult mockResult = new ScanResult();
        mockResult.setBarcode(barcode);
        mockResult.setProductName("Unknown Product (Barcode: " + barcode + ")");

        // Mock ingredients based on barcode pattern
        java.util.List<String> ingredients = new java.util.ArrayList<>();
        if (barcode.startsWith("123")) {
            ingredients.add("Farine de blé");
            ingredients.add("Lait en poudre");
            ingredients.add("Œufs");
            mockResult.setAllergens(java.util.Arrays.asList("Gluten", "Lait", "Œufs"));
            mockResult.setRiskLevel("HIGH");
            mockResult.setHasUserAllergens(true);
        } else if (barcode.startsWith("234")) {
            ingredients.add("Amandes");
            ingredients.add("Sucre");
            ingredients.add("Chocolat");
            mockResult.setAllergens(java.util.Arrays.asList("Fruits à coque"));
            mockResult.setRiskLevel("MEDIUM");
            mockResult.setHasUserAllergens(false);
        } else {
            ingredients.add("Eau");
            ingredients.add("Sucre");
            ingredients.add("Arômes naturels");
            mockResult.setAllergens(new java.util.ArrayList<>());
            mockResult.setRiskLevel("LOW");
            mockResult.setHasUserAllergens(false);
        }

        mockResult.setIngredients(ingredients);
        navigateToResults(mockResult);
    }

    private void navigateToResults(ScanResult result) {
        // Option: Use an existing activity instead
        // Intent intent = new Intent(this, ExistingResultActivity.class);

        Intent intent = new Intent(this, ScanResultActivity.class);
        intent.putExtra("scan_result", result);
        startActivity(intent);
        resetUI();
    }

    private void resetUI() {
        binding.progressBar.setVisibility(View.GONE);
        binding.btnManualSearch.setEnabled(true);
        isScanning = true; // Resume camera scanning
    }

    private void toggleFlash() {
        if (!isCameraInitialized || camera == null) {
            Toast.makeText(this, getString(R.string.camera_not_initialized), Toast.LENGTH_SHORT).show();
            return;
        }

        isFlashOn = !isFlashOn;
        camera.getCameraControl().enableTorch(isFlashOn);

        String flashText = isFlashOn ? getString(R.string.flash_on) : getString(R.string.flash_off);
        binding.btnToggleFlash.setText(flashText);
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
                binding.cameraPreviewContainer.setVisibility(View.GONE);
            }
        }
    }

    private void initializeCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindCameraUseCases();
                isCameraInitialized = true;

                runOnUiThread(() -> {
                    binding.tvScanInstructions.setText(getString(R.string.scanner_instructions));
                    binding.btnToggleFlash.setText(getString(R.string.flash_off));
                });

            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Camera initialization failed", e);
                showError("Camera initialization failed");
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindCameraUseCases() {
        if (cameraProvider == null) return;

        // Preview use case
        preview = new Preview.Builder().build();

        // Create a PreviewView programmatically since it's not in the layout
        PreviewView previewView = new PreviewView(this);
        binding.cameraPreviewContainer.removeAllViews();
        binding.cameraPreviewContainer.addView(previewView);

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        // Image analysis use case for barcode scanning
        imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(cameraExecutor, new BarcodeAnalyzer());

        // Camera selector
        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

        try {
            // Unbind all use cases before rebinding
            cameraProvider.unbindAll();

            // Bind use cases to camera
            camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalysis);

        } catch (Exception e) {
            Log.e(TAG, "Use case binding failed", e);
        }
    }

    private class BarcodeAnalyzer implements ImageAnalysis.Analyzer {
        @Override
        public void analyze(@NonNull ImageProxy imageProxy) {
            if (!isScanning) {
                imageProxy.close();
                return;
            }

            @SuppressWarnings("UnsafeOptInUsageError")
            android.media.Image mediaImage = imageProxy.getImage();
            if (mediaImage != null) {
                InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());

                barcodeScanner.process(image)
                        .addOnSuccessListener(barcodes -> {
                            for (Barcode barcode : barcodes) {
                                String barcodeValue = barcode.getDisplayValue();
                                if (barcodeValue != null && !barcodeValue.isEmpty()) {
                                    onBarcodeDetected(barcodeValue);
                                    break;
                                }
                            }
                        })
                        .addOnFailureListener(e -> Log.e(TAG, "Barcode scanning failed", e))
                        .addOnCompleteListener(task -> imageProxy.close());
            } else {
                imageProxy.close();
            }
        }
    }

    private void onBarcodeDetected(String barcode) {
        if (!isScanning) return;

        runOnUiThread(() -> {
            binding.etManualBarcode.setText(barcode);
            searchProduct(barcode);
        });
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
        if (barcodeScanner != null) {
            barcodeScanner.close();
        }
        binding = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Re-check if user is logged in when activity resumes (same as ProfileActivity pattern)
        if (!authManager.isLoggedIn()) {
            Log.e(TAG, "User not logged in on resume");
            navigateToLogin();
            return;
        }

        AuthManager.MockUserInfo user = authManager.getCurrentUser();
        if (user != null) {
            Log.d(TAG, "Session valid on resume - User ID: " + user.getId());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop scanning when activity is paused
        isScanning = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Additional cleanup when activity stops
        isScanning = false;
    }
}
