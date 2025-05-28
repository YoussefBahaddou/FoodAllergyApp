package ma.emsi.foodallergyapp.ui.scanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import ma.emsi.foodallergyapp.databinding.FragmentScannerBinding;

public class ScannerFragment extends Fragment {

    private FragmentScannerBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentScannerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupClickListeners();
        return root;
    }

    private void setupClickListeners() {
        binding.cardBarcode.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ScannerActivity.class);
            intent.putExtra("scan_type", "barcode");
            startActivity(intent);
        });

        binding.cardName.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ScannerActivity.class);
            intent.putExtra("scan_type", "name");
            startActivity(intent);
        });

        binding.cardIngredients.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ScannerActivity.class);
            intent.putExtra("scan_type", "ingredients");
            startActivity(intent);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}