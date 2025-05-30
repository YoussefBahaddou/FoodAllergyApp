package ma.emsi.foodallergyapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import ma.emsi.foodallergyapp.R;
import ma.emsi.foodallergyapp.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        // Set up map card click listener
        binding.cardMap.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.nav_map);
        });

        // Set up scan card click listener
        binding.cardScan.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.nav_scanner);
        });

        // Set up allergies card click listener
        binding.cardAllergies.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.nav_allergies);
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
