package ma.emsi.foodallergyapp.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import ma.emsi.foodallergyapp.auth.AuthManager;
import ma.emsi.foodallergyapp.auth.LoginActivity;
import ma.emsi.foodallergyapp.databinding.ActivityProfileBinding;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authManager = new AuthManager(this);

        // Check if user is logged in
        if (!authManager.isLoggedIn()) {
            navigateToLogin();
            return;
        }

        setupViews();
        loadUserInfo();
    }

    private void setupViews() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        binding.btnLogout.setOnClickListener(v -> logout());
        binding.btnSaveProfile.setOnClickListener(v -> saveProfile());
    }

    private void loadUserInfo() {
        AuthManager.MockUserInfo user = authManager.getCurrentUser();
        if (user != null) {
            binding.etProfileName.setText(user.getName());
            binding.etProfileEmail.setText(user.getEmail());
            binding.tvUserId.setText("User ID: " + user.getId());

            // Show allergies selection status
            if (user.isAllergiesSelected()) {
                binding.tvAllergiesStatus.setText("Allergies: Configured");
                binding.tvAllergiesStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            } else {
                binding.tvAllergiesStatus.setText("Allergies: Not configured");
                binding.tvAllergiesStatus.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
            }
        }
    }

    private void saveProfile() {
        String name = binding.etProfileName.getText().toString().trim();
        String email = binding.etProfileEmail.getText().toString().trim();

        // Clear previous errors
        binding.etProfileName.setError(null);
        binding.etProfileEmail.setError(null);

        if (name.isEmpty()) {
            binding.etProfileName.setError("Name is required");
            binding.etProfileName.requestFocus();
            return;
        }

        if (name.length() < 2) {
            binding.etProfileName.setError("Name must be at least 2 characters");
            binding.etProfileName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            binding.etProfileEmail.setError("Email is required");
            binding.etProfileEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etProfileEmail.setError("Please enter a valid email");
            binding.etProfileEmail.requestFocus();
            return;
        }

        // Show loading state
        setLoadingState(true);

        authManager.updateUserProfile(name, email, new AuthManager.UpdateCallback() {
            @Override
            public void onSuccess(AuthManager.MockUserInfo user) {
                runOnUiThread(() -> {
                    setLoadingState(false);
                    Toast.makeText(ProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    loadUserInfo(); // Refresh the display
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    setLoadingState(false);

                    // Handle specific error messages
                    String userFriendlyMessage;
                    if (error.contains("Network error")) {
                        userFriendlyMessage = "Network error. Please check your connection and try again.";
                    } else if (error.contains("User not logged in")) {
                        userFriendlyMessage = "Session expired. Please log in again.";
                        navigateToLogin();
                        return;
                    } else {
                        userFriendlyMessage = "Failed to update profile. Please try again.";
                    }

                    Toast.makeText(ProfileActivity.this, userFriendlyMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void setLoadingState(boolean isLoading) {
        binding.btnSaveProfile.setEnabled(!isLoading);
        binding.etProfileName.setEnabled(!isLoading);
        binding.etProfileEmail.setEnabled(!isLoading);
        binding.btnLogout.setEnabled(!isLoading);

        // Update button text to show loading state
        binding.btnSaveProfile.setText(isLoading ? "Saving..." : "Save Profile");
    }

    private void logout() {
        authManager.logout();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        navigateToLogin();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
