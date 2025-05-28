package ma.emsi.foodallergyapp.ui.scanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ma.emsi.foodallergyapp.R;
import ma.emsi.foodallergyapp.databinding.FragmentScannerBinding;
import ma.emsi.foodallergyapp.ui.allergies.AllergySelectionActivity;

public class ScannerFragment extends Fragment {

    private FragmentScannerBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentScannerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();
        setupClickListeners();
    }

    private void setupViews() {
        // Any additional view setup can go here
        // For example, you could set up animations or initial states
    }

    private void setupClickListeners() {
        // Main scanner button - opens the scanner activity
        binding.btnOpenScanner.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(getActivity(), ScannerActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                showError("Unable to open scanner");
            }
        });

        // History button - shows scan history (placeholder for now)
        binding.btnScanHistory.setOnClickListener(v -> {
            // TODO: Navigate to history activity when implemented
            showInfo(getString(R.string.scan_history_coming_soon));
        });

        // Manage allergies button - opens allergy selection
        binding.btnManageAllergies.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(getActivity(), AllergySelectionActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                showError("Unable to open allergy management");
            }
        });
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private void showInfo(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}