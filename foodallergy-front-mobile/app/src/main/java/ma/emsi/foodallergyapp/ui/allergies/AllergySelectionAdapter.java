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

public class AllergySelectionAdapter extends RecyclerView.Adapter<AllergySelectionAdapter.AllergenViewHolder> {

    private List<AllergySelectionActivity.Allergen> allergens;
    private OnAllergenClickListener listener;

    public interface OnAllergenClickListener {
        void onAllergenClick(AllergySelectionActivity.Allergen allergen, boolean isSelected);
    }

    public AllergySelectionAdapter(List<AllergySelectionActivity.Allergen> allergens, OnAllergenClickListener listener) {
        this.allergens = allergens;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AllergenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_allergen_selection, parent, false);
        return new AllergenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllergenViewHolder holder, int position) {
        AllergySelectionActivity.Allergen allergen = allergens.get(position);
        holder.bind(allergen, listener);
    }

    @Override
    public int getItemCount() {
        return allergens != null ? allergens.size() : 0;
    }

    public void updateAllergens(List<AllergySelectionActivity.Allergen> newAllergens) {
        this.allergens = newAllergens;
        notifyDataSetChanged();
    }

    static class AllergenViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAllergenName;
        private TextView tvAllergenDescription;
        private CheckBox cbAllergen;

        public AllergenViewHolder(@NonNull View itemView) {
            super(itemView);
            // Use the correct IDs from the layout
            tvAllergenName = itemView.findViewById(R.id.text_allergen_name);
            tvAllergenDescription = itemView.findViewById(R.id.text_allergen_description);
            cbAllergen = itemView.findViewById(R.id.checkbox_allergen);
        }

        public void bind(AllergySelectionActivity.Allergen allergen, OnAllergenClickListener listener) {
            if (tvAllergenName != null) {
                tvAllergenName.setText(allergen.getName());
            }

            if (tvAllergenDescription != null) {
                tvAllergenDescription.setText(allergen.getDescription());
            }

            if (cbAllergen != null) {
                cbAllergen.setChecked(allergen.isSelected());

                // Set click listener for the entire item
                itemView.setOnClickListener(v -> {
                    boolean newState = !cbAllergen.isChecked();
                    cbAllergen.setChecked(newState);
                    allergen.setSelected(newState);
                    if (listener != null) {
                        listener.onAllergenClick(allergen, newState);
                    }
                });

                // Set click listener for the checkbox
                cbAllergen.setOnClickListener(v -> {
                    boolean isChecked = cbAllergen.isChecked();
                    allergen.setSelected(isChecked);
                    if (listener != null) {
                        listener.onAllergenClick(allergen, isChecked);
                    }
                });
            }
        }
    }
}