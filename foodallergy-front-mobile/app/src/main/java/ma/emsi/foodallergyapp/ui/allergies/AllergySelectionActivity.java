package ma.emsi.foodallergyapp.ui.allergies;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import ma.emsi.foodallergyapp.MainActivity;
import ma.emsi.foodallergyapp.R;
import ma.emsi.foodallergyapp.auth.AuthManager;
import ma.emsi.foodallergyapp.databinding.ActivityAllergySelectionBinding;
import ma.emsi.foodallergyapp.utils.SupabaseClientHelper;
import java.util.ArrayList;
import java.util.List;

public class AllergySelectionActivity extends AppCompatActivity implements AllergySelectionAdapter.OnAllergenClickListener {

    private ActivityAllergySelectionBinding binding;
    private AllergySelectionAdapter adapter;
    private List<Allergen> allergenList;
    private List<Allergen> selectedAllergens;
    private SupabaseClientHelper supabaseClient;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllergySelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeComponents();
        setupToolbar();
        setupRecyclerView();
        setupClickListeners();
        loadAllergens();
    }

    private void initializeComponents() {
        supabaseClient = SupabaseClientHelper.getInstance();
        authManager = AuthManager.getInstance(this);
        allergenList = new ArrayList<>();
        selectedAllergens = new ArrayList<>();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.select_allergies));
        }
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        adapter = new AllergySelectionAdapter(allergenList, this);
        binding.recyclerAllergens.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerAllergens.setAdapter(adapter);
    }

    private void setupClickListeners() {
        binding.btnSaveAllergies.setOnClickListener(v -> saveSelectedAllergies());
        binding.btnSkip.setOnClickListener(v -> skipAllergySelection());
    }

    private void loadAllergens() {
        showLoading(true);

        supabaseClient.getAllergens(new SupabaseClientHelper.AllergenCallback() {
            @Override
            public void onSuccess(List<Allergen> allergens) {
                runOnUiThread(() -> {
                    showLoading(false);
                    allergenList.clear();
                    allergenList.addAll(allergens);
                    adapter.updateAllergens(allergenList);

                    // Load user's existing allergies if any
                    loadUserAllergies();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    showError("Failed to load allergens: " + error);
                });
            }
        });
    }

    private void loadUserAllergies() {
        String userId = authManager.getCurrentUserId();
        if (userId == null) return;

        supabaseClient.getUserAllergies(userId, new SupabaseClientHelper.AllergenCallback() {
            @Override
            public void onSuccess(List<Allergen> userAllergens) {
                runOnUiThread(() -> {
                    // Mark user's existing allergens as selected
                    for (Allergen userAllergen : userAllergens) {
                        for (Allergen allergen : allergenList) {
                            if (allergen.getId().equals(userAllergen.getId())) {
                                allergen.setSelected(true);
                                break;
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                    updateSelectedCount();
                });
            }

            @Override
            public void onError(String error) {
                // It's okay if user doesn't have allergies yet
                runOnUiThread(() -> updateSelectedCount());
            }
        });
    }

    @Override
    public void onAllergenClick(Allergen allergen, boolean isSelected) {
        if (isSelected) {
            if (!selectedAllergens.contains(allergen)) {
                selectedAllergens.add(allergen);
            }
        } else {
            selectedAllergens.remove(allergen);
        }
        updateSelectedCount();
    }

    private void updateSelectedCount() {
        selectedAllergens.clear();
        for (Allergen allergen : allergenList) {
            if (allergen.isSelected()) {
                selectedAllergens.add(allergen);
            }
        }

        int count = selectedAllergens.size();
        String buttonText = count > 0 ?
                getString(R.string.save_allergies) + " (" + count + ")" :
                getString(R.string.save_allergies);
        binding.btnSaveAllergies.setText(buttonText);
        binding.btnSaveAllergies.setEnabled(true);
    }

    private void saveSelectedAllergies() {
        String userId = authManager.getCurrentUserId();
        if (userId == null) {
            showError("User not logged in");
            return;
        }

        showLoading(true);

        List<String> allergenIds = new ArrayList<>();
        for (Allergen allergen : selectedAllergens) {
            allergenIds.add(allergen.getId());
        }

        supabaseClient.saveUserAllergies(userId, allergenIds, new SupabaseClientHelper.SaveCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    showLoading(false);
                    showSuccess(getString(R.string.allergies_saved));
                    navigateToMain();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    showError("Failed to save allergies: " + error);
                });
            }
        });
    }

    private void skipAllergySelection() {
        // User chose to skip allergy selection
        navigateToMain();
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.btnSaveAllergies.setEnabled(!show);
        binding.btnSkip.setEnabled(!show);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    // Inner class for Allergen
    public static class Allergen {
        private String id;
        private String name;
        private String description;
        private boolean selected;

        public Allergen() {}

        public Allergen(String id, String name, String description) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.selected = false;
        }

        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public boolean isSelected() { return selected; }
        public void setSelected(boolean selected) { this.selected = selected; }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Allergen allergen = (Allergen) obj;
            return id != null ? id.equals(allergen.id) : allergen.id == null;
        }

        @Override
        public int hashCode() {
            return id != null ? id.hashCode() : 0;
        }
    }
}
