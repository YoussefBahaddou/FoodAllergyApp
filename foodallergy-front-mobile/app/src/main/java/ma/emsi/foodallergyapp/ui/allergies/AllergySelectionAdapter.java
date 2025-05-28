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

public class AllergySelectionAdapter extends RecyclerView.Adapter<AllergySelectionAdapter.AllergyViewHolder> {

    private List<AllergySelectionActivity.Allergen> allergyItems;

    public AllergySelectionAdapter(List<AllergySelectionActivity.Allergen> allergyItems) {
        this.allergyItems = allergyItems;
    }

    public void updateAllergens(List<AllergySelectionActivity.Allergen> newAllergyItems) {
        this.allergyItems = newAllergyItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AllergyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_allergen, parent, false);
        return new AllergyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllergyViewHolder holder, int position) {
        AllergySelectionActivity.Allergen item = allergyItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return allergyItems != null ? allergyItems.size() : 0;
    }

    static class AllergyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAllergenName;
        private TextView tvAllergenDescription;
        private CheckBox cbAllergen;

        public AllergyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAllergenName = itemView.findViewById(R.id.tv_allergen_name);
            tvAllergenDescription = itemView.findViewById(R.id.tv_allergen_description);
            cbAllergen = itemView.findViewById(R.id.cb_allergen);
        }

        public void bind(AllergySelectionActivity.Allergen item) {
            tvAllergenName.setText(item.getName());
            tvAllergenDescription.setText(item.getDescription());
            cbAllergen.setChecked(item.isSelected());

            cbAllergen.setOnCheckedChangeListener((buttonView, isChecked) -> {
                item.setSelected(isChecked);
            });

            itemView.setOnClickListener(v -> {
                cbAllergen.setChecked(!cbAllergen.isChecked());
            });
        }
    }
}