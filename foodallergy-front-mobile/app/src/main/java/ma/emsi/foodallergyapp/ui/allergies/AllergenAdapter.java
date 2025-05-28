package ma.emsi.foodallergyapp.ui.allergies;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ma.emsi.foodallergyapp.R;
import java.util.List;

public class AllergenAdapter extends RecyclerView.Adapter<AllergenAdapter.AllergenViewHolder> {

    private List<AllergySelectionActivity.Allergen> allergens;
    private OnAllergenClickListener listener;

    public interface OnAllergenClickListener {
        void onAllergenClick(AllergySelectionActivity.Allergen allergen, boolean isSelected);
    }

    public AllergenAdapter(List<AllergySelectionActivity.Allergen> allergens, OnAllergenClickListener listener) {
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
        private TextView textAllergenName;
        private TextView textAllergenDescription;
        private CheckBox checkboxAllergen;

        public AllergenViewHolder(@NonNull View itemView) {
            super(itemView);
            textAllergenName = itemView.findViewById(R.id.text_allergen_name);
            textAllergenDescription = itemView.findViewById(R.id.text_allergen_description);
            checkboxAllergen = itemView.findViewById(R.id.checkbox_allergen);
        }

        public void bind(AllergySelectionActivity.Allergen allergen, OnAllergenClickListener listener) {
            if (textAllergenName != null) {
                textAllergenName.setText(allergen.getName());
            }

            if (textAllergenDescription != null) {
                textAllergenDescription.setText(allergen.getDescription());
            }

            if (checkboxAllergen != null) {
                checkboxAllergen.setChecked(allergen.isSelected());

                // Set click listener for the entire item
                itemView.setOnClickListener(v -> {
                    boolean newState = !checkboxAllergen.isChecked();
                    checkboxAllergen.setChecked(newState);
                    allergen.setSelected(newState);
                    if (listener != null) {
                        listener.onAllergenClick(allergen, newState);
                    }
                });

                // Set click listener for the checkbox
                checkboxAllergen.setOnClickListener(v -> {
                    boolean isChecked = checkboxAllergen.isChecked();
                    allergen.setSelected(isChecked);
                    if (listener != null) {
                        listener.onAllergenClick(allergen, isChecked);
                    }
                });
            }
        }
    }
}
