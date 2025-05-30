package ma.emsi.foodallergyapp.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import ma.emsi.foodallergyapp.R;
import ma.emsi.foodallergyapp.model.ScanHistory;

public class RecentScansAdapter extends RecyclerView.Adapter<RecentScansAdapter.ScanViewHolder> {

    private List<ScanHistory> scans;

    public RecentScansAdapter(List<ScanHistory> scans) {
        this.scans = scans;
    }

    @NonNull
    @Override
    public ScanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_scan_history, parent, false);
        return new ScanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScanViewHolder holder, int position) {
        ScanHistory scan = scans.get(position);
        holder.bind(scan);
    }

    @Override
    public int getItemCount() {
        return scans != null ? scans.size() : 0;
    }

    public void updateScans(List<ScanHistory> newScans) {
        this.scans = newScans;
        notifyDataSetChanged();
    }

    static class ScanViewHolder extends RecyclerView.ViewHolder {
        private final TextView textProductName;
        private final TextView textStatus;
        private final TextView textTime;

        public ScanViewHolder(@NonNull View itemView) {
            super(itemView);
            textProductName = itemView.findViewById(R.id.text_product_name);
            textStatus = itemView.findViewById(R.id.text_status);
            textTime = itemView.findViewById(R.id.text_time);
        }

        public void bind(ScanHistory scan) {
            if (scan == null) return;

            // Set product name with null check
            textProductName.setText(scan.getProductName() != null ? scan.getProductName() : "Unknown Product");

            // Set status with null check
            String status = scan.getStatus();
            textStatus.setText(status);

            // Set time with null check
            textTime.setText(scan.getTimeAgo());

            // Set status color based on safety
            int statusColor;
            if (scan.isSafe()) {
                statusColor = itemView.getContext().getColor(R.color.success);
            } else {
                statusColor = itemView.getContext().getColor(R.color.error);
            }
            textStatus.setTextColor(statusColor);
        }
    }
} 