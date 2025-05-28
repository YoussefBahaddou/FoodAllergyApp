package ma.emsi.foodallergyapp.ui.allergies;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import ma.emsi.foodallergyapp.databinding.FragmentAllergiesBinding;

public class AllergiesFragment extends Fragment {

    private FragmentAllergiesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAllergiesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupClickListeners();
        return root;
    }

    private void setupClickListeners() {
        binding.btnManageAllergies.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AllergySelectionActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}