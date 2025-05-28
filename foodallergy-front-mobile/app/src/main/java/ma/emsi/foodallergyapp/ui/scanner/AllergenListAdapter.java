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

    public void updateAllergens(List<String> newAllergens) {
        this.allergens = newAllergens;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AllergenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_allergen_simple, parent, false);
        return new AllergenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllergenViewHolder holder, int position) {
        String allergen = allergens.get(position);
        holder.bind(allergen);
    }

    @Override
    public int getItemCount() {
        return allergens != null ? allergens.size() : 0;
    }

    static class AllergenViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAllergenName;

        public AllergenViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAllergenName = itemView.findViewById(R.id.tv_allergen_name);
        }

        public void bind(String allergen) {
            tvAllergenName.setText(allergen);
        }
    }
}