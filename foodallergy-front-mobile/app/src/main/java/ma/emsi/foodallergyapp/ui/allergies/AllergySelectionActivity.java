package ma.emsi.foodallergyapp.ui.allergies;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ma.emsi.foodallergyapp.MainActivity;
import ma.emsi.foodallergyapp.auth.AuthManager;
import ma.emsi.foodallergyapp.auth.LoginActivity;
import ma.emsi.foodallergyapp.databinding.ActivityAllergySelectionBinding;
import ma.emsi.foodallergyapp.utils.SupabaseClientHelper;
import java.util.ArrayList;
import java.util.List;

public class AllergySelectionActivity extends AppCompatActivity {

    private static final String TAG = "AllergySelectionActivity";
    private ActivityAllergySelectionBinding binding;
    private AllergenAdapter adapter;
    private List<Allergen> allergenList;
    private List<Allergen> selectedAllergens;
    private SupabaseClientHelper supabaseClient;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllergySelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authManager = new AuthManager(this);
        supabaseClient = SupabaseClientHelper.getInstance();

        // Check if user is logged in
        if (!authManager.isLoggedIn()) {
            navigateToLogin();
            return;
        }

        setupViews();
        setupRecyclerView();
        loadAllergens();
    }

    private void setupViews() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Select Your Allergies");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        binding.btnSaveAllergies.setOnClickListener(v -> saveSelectedAllergies());
        binding.btnSkip.setOnClickListener(v -> skipAllergySelection());
    }

    private void setupRecyclerView() {
        allergenList = new ArrayList<>();
        selectedAllergens = new ArrayList<>();

        adapter = new AllergenAdapter(allergenList, new AllergenAdapter.OnAllergenClickListener() {
            @Override
            public void onAllergenClick(Allergen allergen, boolean isSelected) {
                if (isSelected) {
                    if (!selectedAllergens.contains(allergen)) {
                        selectedAllergens.add(allergen);
                    }
                } else {
                    selectedAllergens.remove(allergen);
                }
                updateSaveButton();
            }
        });

        binding.recyclerViewAllergens.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewAllergens.setAdapter(adapter);
    }

    private void loadAllergens() {
        setLoadingState(true);

        // Load all allergens from database
        supabaseClient.getAllergens(new SupabaseClientHelper.AllergenCallback() {
            @Override
            public void onSuccess(List<Allergen> allergens) {
                runOnUiThread(() -> {
                    allergenList.clear();
                    allergenList.addAll(allergens);
                    adapter.notifyDataSetChanged();

                    // Load user's existing allergies to pre-select them
                    loadUserAllergies();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    setLoadingState(false);
                    Log.e(TAG, "Failed to load allergens: " + error);
                    Toast.makeText(AllergySelectionActivity.this,
                        "Failed to load allergens. Please try again.", Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void loadUserAllergies() {
        String userId = authManager.getCurrentUserId();
        if (userId == null) {
            setLoadingState(false);
            return;
        }

        supabaseClient.getUserAllergies(userId, new SupabaseClientHelper.AllergenCallback() {
            @Override
            public void onSuccess(List<Allergen> userAllergens) {
                runOnUiThread(() -> {
                    setLoadingState(false);

                    // Pre-select user's existing allergies
                    selectedAllergens.clear();
                    for (Allergen userAllergen : userAllergens) {
                        for (Allergen allergen : allergenList) {
                            if (allergen.getId().equals(userAllergen.getId())) {
                                selectedAllergens.add(allergen);
                                allergen.setSelected(true);
                                break;
                            }
                        }
                    }

                    adapter.notifyDataSetChanged();
                    updateSaveButton();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    setLoadingState(false);
                    Log.e(TAG, "Failed to load user allergies: " + error);
                    // Don't show error to user as this is not critical
                    // User can still select allergies normally
                });
            }
        });
    }

    private void saveSelectedAllergies() {
        String userId = authManager.getCurrentUserId();
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            navigateToLogin();
            return;
        }

        setLoadingState(true);

        // Convert selected allergens to IDs
        List<String> allergenIds = new ArrayList<>();
        for (Allergen allergen : selectedAllergens) {
            allergenIds.add(allergen.getId());
        }

        supabaseClient.saveUserAllergies(userId, allergenIds, new SupabaseClientHelper.SaveCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    setLoadingState(false);

                    // Update local auth manager
                    authManager.updateAllergiesSelected(true);

                    Toast.makeText(AllergySelectionActivity.this,
                        "Allergies saved successfully!", Toast.LENGTH_SHORT).show();

                    navigateToMain();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    setLoadingState(false);
                    Log.e(TAG, "Failed to save allergies: " + error);
                    Toast.makeText(AllergySelectionActivity.this,
                        "Failed to save allergies. Please try again.", Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void skipAllergySelection() {
        // User chose to skip allergy selection
        navigateToMain();
    }

    private void updateSaveButton() {
        boolean hasSelections = !selectedAllergens.isEmpty();
        binding.btnSaveAllergies.setEnabled(hasSelections);
        binding.btnSaveAllergies.setText(hasSelections ?
            "Save " + selectedAllergens.size() + " Allergies" : "Save Allergies");
    }

    private void setLoadingState(boolean isLoading) {
        if (binding.progressBar != null) {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }

        binding.btnSaveAllergies.setEnabled(!isLoading && !selectedAllergens.isEmpty());
        binding.btnSkip.setEnabled(!isLoading);
        binding.recyclerViewAllergens.setEnabled(!isLoading);

        if (isLoading) {
            binding.btnSaveAllergies.setText("Saving...");
        } else {
            updateSaveButton();
        }
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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

    // Allergen model class
    public static class Allergen {
        private String id;
        private String name;
        private String description;
        private boolean isSelected;

        public Allergen(String id, String name, String description) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.isSelected = false;
        }

        // Getters and setters
        public String getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public boolean isSelected() { return isSelected; }

        public void setId(String id) { this.id = id; }
        public void setName(String name) { this.name = name; }
        public void setDescription(String description) { this.description = description; }
        public void setSelected(boolean selected) { this.isSelected = selected; }

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
