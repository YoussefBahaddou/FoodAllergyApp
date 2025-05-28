package ma.emsi.foodallergyapp.ui.allergies;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ma.emsi.foodallergyapp.R;

public class AllergenAdapter extends RecyclerView.Adapter<AllergenAdapter.AllergenViewHolder> {

    private List<AllergySelectionActivity.Allergen> allergens;

    public AllergenAdapter(List<AllergySelectionActivity.Allergen> allergens) {
        this.allergens = allergens;
    }

    public void updateAllergens(List<AllergySelectionActivity.Allergen> newAllergens) {
        this.allergens = newAllergens;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AllergenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_allergen, parent, false);
        return new AllergenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllergenViewHolder holder, int position) {
        AllergySelectionActivity.Allergen allergen = allergens.get(position);
        holder.bind(allergen);
    }

    @Override
    public int getItemCount() {
        return allergens != null ? allergens.size() : 0;
    }

    static class AllergenViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAllergenName;
        private TextView tvAllergenDescription;
        private CheckBox cbAllergen;

        public AllergenViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAllergenName = itemView.findViewById(R.id.tv_allergen_name);
            tvAllergenDescription = itemView.findViewById(R.id.tv_allergen_description);
            cbAllergen = itemView.findViewById(R.id.cb_allergen);
        }

        public void bind(AllergySelectionActivity.Allergen allergen) {
            tvAllergenName.setText(allergen.getName());
            tvAllergenDescription.setText(allergen.getDescription());
            cbAllergen.setChecked(allergen.isSelected());

            cbAllergen.setOnCheckedChangeListener((buttonView, isChecked) -> {
                allergen.setSelected(isChecked);
            });

            itemView.setOnClickListener(v -> {
                cbAllergen.setChecked(!cbAllergen.isChecked());
            });
        }
    }
}