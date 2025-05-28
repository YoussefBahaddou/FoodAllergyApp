package ma.emsi.foodallergyapp.ui.scanner;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ma.emsi.foodallergyapp.R;
import ma.emsi.foodallergyapp.databinding.ActivityScanResultBinding;
import ma.emsi.foodallergyapp.models.ScanResult;

public class ScanResultActivity extends AppCompatActivity {

    private ActivityScanResultBinding binding;
    private ScanResult scanResult;
    private AllergenListAdapter allergenAdapter;
    private IngredientListAdapter ingredientAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScanResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupRecyclerViews();
        loadScanResult();
        displayResults();
        setupClickListeners();
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Résultat du scan");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerViews() {
        // Setup allergens recycler view
        allergenAdapter = new AllergenListAdapter(new ArrayList<>());
        binding.recyclerAllergens.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerAllergens.setAdapter(allergenAdapter);

        // Setup ingredients recycler view
        ingredientAdapter = new IngredientListAdapter(new ArrayList<>());
        binding.recyclerIngredients.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerIngredients.setAdapter(ingredientAdapter);
    }

    private void loadScanResult() {
        // Get scan result from intent or create mock data
        String barcode = getIntent().getStringExtra("barcode");

        if (barcode != null) {
            // In a real app, you would fetch this data from your API
            scanResult = createMockScanResult(barcode);
        } else {
            // Create default mock data for testing
            scanResult = createMockScanResult("1234567890123");
        }
    }

    private ScanResult createMockScanResult(String barcode) {
        List<String> ingredients = Arrays.asList(
                "Farine de blé", "Sucre", "Huile de palme", "Cacao en poudre",
                "Lait en poudre", "Œufs", "Sel", "Levure chimique", "Arôme vanille"
        );

        List<String> allergens = Arrays.asList("Gluten", "Lait", "Œufs");

        return new ScanResult(
                "Biscuits au Chocolat",
                barcode,
                ingredients,
                allergens,
                true, // Has user allergens
                "HIGH" // Risk level
        );
    }

    private void displayResults() {
        if (scanResult == null) return;

        // Display product name
        binding.textProductName.setText(scanResult.getProductName());

        // Display barcode
        binding.textBarcode.setText("Code-barres: " + scanResult.getBarcode());

        // Display risk level
        displayRiskLevel();

        // Update adapters
        if (scanResult.getAllergens() != null) {
            allergenAdapter.updateAllergens(scanResult.getAllergens());
        }

        if (scanResult.getIngredients() != null) {
            ingredientAdapter.updateIngredients(scanResult.getIngredients());
        }

        // Show/hide warning
        if (scanResult.isHasUserAllergens()) {
            binding.layoutWarning.setVisibility(View.VISIBLE);
            binding.textWarning.setText("⚠️ Ce produit contient des allergènes qui vous concernent!");
        } else {
            binding.layoutWarning.setVisibility(View.GONE);
        }
    }

    private void displayRiskLevel() {
        String riskLevel = scanResult.getRiskLevel();
        if (riskLevel == null) riskLevel = "LOW";

        switch (riskLevel) {
            case "HIGH":
                binding.textRiskLevel.setText("Risque élevé");
                binding.textRiskLevel.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                break;
            case "MEDIUM":
                binding.textRiskLevel.setText("Risque modéré");
                binding.textRiskLevel.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                break;
            case "LOW":
            default:
                binding.textRiskLevel.setText("Risque faible");
                binding.textRiskLevel.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                break;
        }
    }

    private void setupClickListeners() {
        binding.btnSaveToHistory.setOnClickListener(v -> {
            // TODO: Save to history database
            Toast.makeText(this, "Résultat sauvegardé dans l'historique", Toast.LENGTH_SHORT).show();
        });

        binding.btnShareResult.setOnClickListener(v -> {
            // TODO: Implement sharing functionality
            Toast.makeText(this, "Fonctionnalité de partage à venir", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
