package ma.emsi.foodallergyapp.ui.scanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ma.emsi.foodallergyapp.R;

public class AllergenListAdapter extends RecyclerView.Adapter<AllergenListAdapter.AllergenViewHolder> {

    private List<String> allergens;

    public AllergenListAdapter(List<String> allergens) {
        this.allergens = allergens;
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
        String allergen = allergens.get(position);
        holder.bind(allergen);
    }

    @Override
    public int getItemCount() {
        return allergens.size();
    }

    public void updateAllergens(List<String> newAllergens) {
        this.allergens = newAllergens;
        notifyDataSetChanged();
    }

    static class AllergenViewHolder extends RecyclerView.ViewHolder {
        private TextView textAllergenName;

        public AllergenViewHolder(@NonNull View itemView) {
            super(itemView);
            textAllergenName = itemView.findViewById(R.id.text_allergen_name);
        }

        public void bind(String allergen) {
            textAllergenName.setText(allergen);
        }
    }
}