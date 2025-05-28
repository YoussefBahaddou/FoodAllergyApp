package ma.emsi.foodallergyapp.ui.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ma.emsi.foodallergyapp.R;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<HistoryFragment.HistoryItem> historyItems;

    public HistoryAdapter(List<HistoryFragment.HistoryItem> historyItems) {
        this.historyItems = historyItems;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        HistoryFragment.HistoryItem item = historyItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return historyItems.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        private TextView textProductName;
        private TextView textResult;
        private TextView textTimestamp;
        private ImageView iconStatus;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textProductName = itemView.findViewById(R.id.text_product_name);
            textResult = itemView.findViewById(R.id.text_result);
            textTimestamp = itemView.findViewById(R.id.text_timestamp);
            iconStatus = itemView.findViewById(R.id.icon_status);
        }

        public void bind(HistoryFragment.HistoryItem item) {
            textProductName.setText(item.getProductName());
            textResult.setText(item.getResult());
            textTimestamp.setText(item.getTimestamp());

            if (item.isSafe()) {
                iconStatus.setImageResource(R.drawable.ic_check_circle);
                iconStatus.setColorFilter(itemView.getContext().getColor(R.color.success));
                textResult.setTextColor(itemView.getContext().getColor(R.color.success));
            } else {
                iconStatus.setImageResource(R.drawable.ic_warning);
                iconStatus.setColorFilter(itemView.getContext().getColor(R.color.warning));
                textResult.setTextColor(itemView.getContext().getColor(R.color.warning));
            }
        }
    }
}
