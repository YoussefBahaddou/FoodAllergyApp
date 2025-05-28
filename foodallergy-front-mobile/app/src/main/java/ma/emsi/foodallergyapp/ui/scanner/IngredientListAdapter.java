package ma.emsi.foodallergyapp.ui.scanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ma.emsi.foodallergyapp.R;
import java.util.List;

public class IngredientListAdapter extends RecyclerView.Adapter<IngredientListAdapter.IngredientViewHolder> {

    private List<String> ingredients;

    public IngredientListAdapter(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        String ingredient = ingredients.get(position);
        holder.bind(ingredient);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public void updateIngredients(List<String> newIngredients) {
        this.ingredients = newIngredients;
        notifyDataSetChanged();
    }

    static class IngredientViewHolder extends RecyclerView.ViewHolder {
        private TextView textIngredientName;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            textIngredientName = itemView.findViewById(R.id.text_ingredient_name);
        }

        public void bind(String ingredient) {
            textIngredientName.setText(ingredient);
        }
    }
}
