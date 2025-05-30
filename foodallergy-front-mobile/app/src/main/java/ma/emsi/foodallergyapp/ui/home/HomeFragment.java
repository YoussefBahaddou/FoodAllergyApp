package ma.emsi.foodallergyapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ma.emsi.foodallergyapp.R;
import ma.emsi.foodallergyapp.databinding.FragmentHomeBinding;
import ma.emsi.foodallergyapp.model.ScanHistory;
import ma.emsi.foodallergyapp.utils.SessionManager;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private SessionManager sessionManager;
    private RecentScansAdapter recentScansAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        sessionManager = new SessionManager(requireContext());

        setupViews();
        setupClickListeners();
        setupRecentScans();
        setupSafetyChart();
        updateStatistics();

        return binding.getRoot();
    }

    private void setupViews() {
        // Set welcome message with user's name
        String userName = sessionManager.getUserName();
        binding.textWelcome.setText("Welcome, " + (userName != null ? userName : "User") + "!");

        // Set allergy status
        boolean allergiesSelected = sessionManager.areAllergiesSelected();
        binding.textAllergyStatus.setText(allergiesSelected ? 
            "Your allergy status is up to date" : 
            "Please set up your allergies");
        binding.textAllergyStatus.setTextColor(getResources().getColor(
            allergiesSelected ? R.color.success : R.color.warning));
    }

    private void setupClickListeners() {
        // Set up map card click listener
        binding.cardMap.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.nav_map);
        });

        // Set up scan card click listener
        binding.cardScan.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.nav_scanner);
        });

        // Set up emergency contacts button
        binding.btnEmergencyContacts.setOnClickListener(v -> {
            // TODO: Navigate to emergency contacts
            // For now, show a toast
            android.widget.Toast.makeText(requireContext(), 
                "Emergency contacts feature coming soon", 
                android.widget.Toast.LENGTH_SHORT).show();
        });
    }

    private void setupRecentScans() {
        recentScansAdapter = new RecentScansAdapter(new ArrayList<>());
        binding.recyclerRecentScans.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerRecentScans.setAdapter(recentScansAdapter);

        // Load recent scans
        loadRecentScans();
    }

    private void loadRecentScans() {
        List<ScanHistory> sampleScans = new ArrayList<>();
        sampleScans.add(new ScanHistory(UUID.randomUUID(), "Product 1", "Safe", true));
        sampleScans.add(new ScanHistory(UUID.randomUUID(), "Product 2", "Contains allergens", false));
        sampleScans.add(new ScanHistory(UUID.randomUUID(), "Product 3", "Safe", true));
        recentScansAdapter.updateScans(sampleScans);
    }

    private void setupSafetyChart() {
        PieChart chart = binding.chartSafety;
        
        // Configure chart appearance
        chart.getDescription().setEnabled(false);
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(android.graphics.Color.WHITE);
        chart.setTransparentCircleRadius(30f);
        chart.setHoleRadius(30f);
        chart.setRotationAngle(0);
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);

        // Create sample data
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(85f, "Safe"));
        entries.add(new PieEntry(15f, "Unsafe"));

        PieDataSet dataSet = new PieDataSet(entries, "Product Safety");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(android.graphics.Color.WHITE);

        PieData data = new PieData(dataSet);
        chart.setData(data);
        chart.invalidate(); // refresh
    }

    private void updateStatistics() {
        // TODO: Update with actual statistics from your data source
        binding.textScansToday.setText("12");
        binding.textSafeProducts.setText("85%");
        binding.textAllergiesCount.setText("3");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
