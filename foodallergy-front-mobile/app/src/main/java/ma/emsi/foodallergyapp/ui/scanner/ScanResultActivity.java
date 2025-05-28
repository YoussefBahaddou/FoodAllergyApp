package ma.emsi.foodallergyapp.ui.scanner;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import ma.emsi.foodallergyapp.R;
import ma.emsi.foodallergyapp.databinding.ActivityScanResultBinding;
import ma.emsi.foodallergyapp.model.ScanResult;

public class ScanResultActivity extends AppCompatActivity {

    private ActivityScanResultBinding binding;
    private ScanResult scanResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScanResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get scan result from intent
        scanResult = (ScanResult) getIntent().getSerializableExtra("scan_result");

        if (scanResult == null) {
            finish();
            return;
        }

        setupToolbar();
        displayResults();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Scan Results");
        }
    }

    private void displayResults() {
        // Display product name
        binding.tvProductName.setText(scanResult.getProductName());
        binding.tvBarcode.setText("Barcode: " + scanResult.getBarcode());

        // Display risk level with appropriate color
        String riskLevel = scanResult.getRiskLevel();
        binding.tvRiskLevel.setText("Risk Level: " + riskLevel);

        switch (riskLevel) {
            case "HIGH":
                binding.tvRiskLevel.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
                binding.cardRiskLevel.setCardBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
                break;
            case "MEDIUM":
                binding.tvRiskLevel.setTextColor(ContextCompat.getColor(this, android.R.color.holo_orange_dark));
                binding.cardRiskLevel.setCardBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_orange_light));
                break;
            case "LOW":
                binding.tvRiskLevel.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
                binding.cardRiskLevel.setCardBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_light));
                break;
        }

        // Display allergens
        if (scanResult.getAllergens() != null && !scanResult.getAllergens().isEmpty()) {
            binding.tvAllergens.setText("Allergens: " + String.join(", ", scanResult.getAllergens()));
            binding.tvAllergens.setVisibility(View.VISIBLE);
        } else {
            binding.tvAllergens.setText("No known allergens");
            binding.tvAllergens.setVisibility(View.VISIBLE);
        }

        // Display ingredients
        if (scanResult.getIngredients() != null && !scanResult.getIngredients().isEmpty()) {
            binding.tvIngredients.setText("Ingredients: " + String.join(", ", scanResult.getIngredients()));
            binding.tvIngredients.setVisibility(View.VISIBLE);
        } else {
            binding.tvIngredients.setText("Ingredients not available");
            binding.tvIngredients.setVisibility(View.VISIBLE);
        }

        // Show warning if user has allergens in this product
        if (scanResult.isHasUserAllergens()) {
            binding.tvWarning.setText("⚠️ WARNING: This product contains allergens you're sensitive to!");
            binding.tvWarning.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
            binding.tvWarning.setVisibility(View.VISIBLE);
        } else {
            binding.tvWarning.setText("✅ This product appears safe for you");
            binding.tvWarning.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
            binding.tvWarning.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
