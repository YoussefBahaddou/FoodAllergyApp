package ma.emsi.foodallergyapp.ui.allergies;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ma.emsi.foodallergyapp.databinding.FragmentAllergiesBinding;

public class AllergiesFragment extends Fragment {

    private FragmentAllergiesBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAllergiesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();
    }

    private void setupViews() {
        binding.textAllergies.setText("Manage your allergies here.\n\nClick the button below to select your allergies.");

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
