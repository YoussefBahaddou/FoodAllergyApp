package ma.emsi.foodallergyapp.ui.slideshow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import ma.emsi.foodallergyapp.databinding.FragmentSlideshowBinding;

public class SlideshowFragment extends Fragment {

    private FragmentSlideshowBinding binding;
    private SlideshowViewModel slideshowViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel = new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupObservers();
        setupClickListeners();

        return root;
    }

    private void setupObservers() {
        slideshowViewModel.getText().observe(getViewLifecycleOwner(), text -> {
            if (binding.textSlideshow != null) {
                binding.textSlideshow.setText(text);
            }
        });

        slideshowViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            // Handle loading state if needed
            if (binding.progressBar != null) {
                binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void setupClickListeners() {
        // Add click listeners for allergy management functionality
        if (binding.btnManageAllergies != null) {
            binding.btnManageAllergies.setOnClickListener(v -> openAllergyManagement());
        }
    }

    private void openAllergyManagement() {
        // TODO: Implement allergy management functionality
        if (getContext() != null) {
            android.widget.Toast.makeText(getContext(), "Gestion des allergies à venir", android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
