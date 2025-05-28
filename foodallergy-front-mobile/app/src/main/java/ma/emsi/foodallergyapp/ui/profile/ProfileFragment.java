package ma.emsi.foodallergyapp.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ma.emsi.foodallergyapp.auth.AuthManager;
import ma.emsi.foodallergyapp.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private AuthManager authManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authManager = new AuthManager(requireContext());
        setupViews();
        loadUserInfo();
    }

    private void setupViews() {
        binding.btnOpenProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            startActivity(intent);
        });
    }

    private void loadUserInfo() {
        AuthManager.MockUserInfo user = authManager.getCurrentUser();
        if (user != null) {
            binding.textProfile.setText("Welcome, " + user.getName() + "!\n\n" +
                    "Email: " + user.getEmail() + "\n\n" +
                    "Click the button below to manage your profile.");
        } else {
            binding.textProfile.setText("Profile information not available.");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}