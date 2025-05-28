package ma.emsi.foodallergyapp.ui.allergies;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import ma.emsi.foodallergyapp.R;

public class AllergySelectionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AllergenAdapter adapter; // Use the existing AllergenAdapter
    private MaterialButton btnSave;
    private List<Allergen> allergens;

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
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allergy_selection);

        initViews();
        setupToolbar();
        setupRecyclerView();
        loadAllergens();
        setupSaveButton();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_allergens);
        btnSave = findViewById(R.id.btn_save_allergies);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Sélectionner vos allergies");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AllergenAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
    }

    private void loadAllergens() {
        allergens = new ArrayList<>();

        // Add sample allergens (you can load these from your database later)
        allergens.add(new Allergen("1", "Amlou (Arachides)", "Contient des arachides, très courant dans les produits Amlou."));
        allergens.add(new Allergen("2", "Lait de Chèvre", "Présent dans les fromages traditionnels comme Jben."));
        allergens.add(new Allergen("3", "Gluten (Pain traditionnel)", "Présent dans le pain khobz, msemen, harcha..."));
        allergens.add(new Allergen("4", "Fruits à coque (amandes)", "Utilisés dans les gâteaux marocains comme le kaab ghzal."));
        allergens.add(new Allergen("5", "Fruits de mer", "Allergène commun dans les tajines de poisson, crevettes, etc."));
        allergens.add(new Allergen("6", "Œufs", "Présents dans de nombreuses pâtisseries et plats traditionnels."));
        allergens.add(new Allergen("7", "Soja", "Peut être présent dans certains produits transformés."));
        allergens.add(new Allergen("8", "Sésame", "Utilisé dans certaines préparations et pains."));

        adapter.updateAllergens(allergens);
    }

    private void setupSaveButton() {
        btnSave.setOnClickListener(v -> saveSelectedAllergies());
    }

    private void saveSelectedAllergies() {
        List<Allergen> selectedAllergens = new ArrayList<>();
        for (Allergen allergen : allergens) {
            if (allergen.isSelected()) {
                selectedAllergens.add(allergen);
            }
        }

        // TODO: Save to database or preferences
        Toast.makeText(this,
                "Allergies sauvegardées: " + selectedAllergens.size() + " sélectionnées",
                Toast.LENGTH_SHORT).show();

        finish();
    }
}
