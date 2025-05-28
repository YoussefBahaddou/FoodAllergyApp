package ma.emsi.foodallergyapp.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import ma.emsi.foodallergyapp.MainActivity;
import ma.emsi.foodallergyapp.databinding.ActivitySignupBinding;

public class SignupActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authManager = new AuthManager(this);
        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.btnSignup.setOnClickListener(v -> performSignup());

        // Check if tvLogin exists before setting click listener
        if (binding.tvLogin != null) {
            binding.tvLogin.setOnClickListener(v -> {
                finish(); // Go back to login
            });
        }
    }

    private void performSignup() {
        String name = binding.etName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();

        // Clear previous errors
        binding.etName.setError(null);
        binding.etEmail.setError(null);
        binding.etPassword.setError(null);
        binding.etConfirmPassword.setError(null);

        // Validate inputs
        if (name.isEmpty()) {
            binding.etName.setError("Name is required");
            binding.etName.requestFocus();
            return;
        }

        if (name.length() < 2) {
            binding.etName.setError("Name must be at least 2 characters");
            binding.etName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            binding.etEmail.setError("Email is required");
            binding.etEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.setError("Please enter a valid email");
            binding.etEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            binding.etPassword.setError("Password is required");
            binding.etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            binding.etPassword.setError("Password must be at least 6 characters");
            binding.etPassword.requestFocus();
            return;
        }

        if (confirmPassword.isEmpty()) {
            binding.etConfirmPassword.setError("Please confirm your password");
            binding.etConfirmPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            binding.etConfirmPassword.setError("Passwords do not match");
            binding.etConfirmPassword.requestFocus();
            return;
        }

        // Show loading
        setLoadingState(true);

        // Perform signup using Supabase
        authManager.signup(name, email, password, new AuthManager.SignupCallback() {
            @Override
            public void onSuccess(AuthManager.MockUserInfo user) {
                runOnUiThread(() -> {
                    setLoadingState(false);
                    Toast.makeText(SignupActivity.this, "Welcome, " + user.getName() + "!", Toast.LENGTH_SHORT).show();
                    navigateToMain();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    setLoadingState(false);

                    // Handle specific error messages
                    String userFriendlyMessage;
                    if (error.contains("Email already exists")) {
                        userFriendlyMessage = "An account with this email already exists. Please use a different email or try logging in.";
                    } else if (error.contains("Network error")) {
                        userFriendlyMessage = "Network error. Please check your connection and try again.";
                    } else {
                        userFriendlyMessage = "Signup failed. Please try again.";
                    }

                    Toast.makeText(SignupActivity.this, userFriendlyMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void setLoadingState(boolean isLoading) {
        if (binding.progressBar != null) {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
        binding.btnSignup.setEnabled(!isLoading);
        binding.etName.setEnabled(!isLoading);
        binding.etEmail.setEnabled(!isLoading);
        binding.etPassword.setEnabled(!isLoading);
        binding.etConfirmPassword.setEnabled(!isLoading);
        if (binding.tvLogin != null) {
            binding.tvLogin.setEnabled(!isLoading);
        }

        // Update button text to show loading state
        binding.btnSignup.setText(isLoading ? "Creating account..." : "Sign Up");
    }

    private void navigateToMain() {
        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
