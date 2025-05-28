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
        return allergens.size();
    }

    class AllergenViewHolder extends RecyclerView.ViewHolder {
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

            // Handle checkbox clicks
            cbAllergen.setOnCheckedChangeListener(null); // Clear previous listener
            cbAllergen.setOnCheckedChangeListener((buttonView, isChecked) -> {
                allergen.setSelected(isChecked);
                if (listener != null) {
                    listener.onAllergenClick(allergen, isChecked);
                }
            });

            // Handle item clicks
            itemView.setOnClickListener(v -> {
                boolean newState = !allergen.isSelected();
                allergen.setSelected(newState);
                cbAllergen.setChecked(newState);
                if (listener != null) {
                    listener.onAllergenClick(allergen, newState);
                }
            });
        }
    }
}
