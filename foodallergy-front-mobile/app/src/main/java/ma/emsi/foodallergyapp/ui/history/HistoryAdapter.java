package ma.emsi.foodallergyapp.ui.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import ma.emsi.foodallergyapp.R;
import ma.emsi.foodallergyapp.model.ScanHistory;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<ScanHistory> historyList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());

    public HistoryAdapter(List<ScanHistory> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        ScanHistory history = historyList.get(position);
        holder.bind(history);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public void updateHistory(List<ScanHistory> newHistoryList) {
        this.historyList = newHistoryList;
        notifyDataSetChanged();
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {
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

        public void bind(ScanHistory history) {
            // Set product name or scan input
            if (history.getProductName() != null && !history.getProductName().isEmpty()) {
                textProductName.setText(history.getProductName());
            } else {
                textProductName.setText("Product: " + history.getScanInput());
            }

            // Set result text and icon based on safety
            if (history.isSafe()) {
                textResult.setText("✓ Safe to consume");
                textResult.setTextColor(itemView.getContext().getColor(R.color.success));
                iconStatus.setImageResource(R.drawable.ic_check_circle);
                iconStatus.setColorFilter(itemView.getContext().getColor(R.color.success));
            } else {
                textResult.setText("⚠ Contains allergens");
                textResult.setTextColor(itemView.getContext().getColor(R.color.error));
                iconStatus.setImageResource(R.drawable.ic_warning);
                iconStatus.setColorFilter(itemView.getContext().getColor(R.color.error));
            }

            // Set timestamp
            if (history.getScannedAt() != null) {
                textTimestamp.setText(dateFormat.format(history.getScannedAt()));
            } else {
                textTimestamp.setText("Unknown date");
            }

            // Set click listener for item details
            itemView.setOnClickListener(v -> {
                // TODO: Navigate to scan result details
                // You can implement this later to show full scan details
            });
        }
    }
}
