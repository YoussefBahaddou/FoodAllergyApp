package ma.emsi.foodallergyapp.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import ma.emsi.foodallergyapp.MainActivity;
import ma.emsi.foodallergyapp.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authManager = new AuthManager(this);

        // Check if user is already logged in
        if (authManager.isLoggedIn()) {
            navigateToMain();
            return;
        }

        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.btnLogin.setOnClickListener(v -> performLogin());

        // Check if tvSignup exists before setting click listener
        if (binding.tvSignup != null) {
            binding.tvSignup.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            });
        }
    }

    private void performLogin() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        // Clear previous errors
        binding.etEmail.setError(null);
        binding.etPassword.setError(null);

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

        // Show loading
        setLoadingState(true);

        // Perform login using Supabase
        authManager.login(email, password, new AuthManager.LoginCallback() {
            @Override
            public void onSuccess(AuthManager.MockUserInfo user) {
                runOnUiThread(() -> {
                    setLoadingState(false);
                    Toast.makeText(LoginActivity.this, "Welcome back, " + user.getName() + "!", Toast.LENGTH_SHORT).show();
                    navigateToMain();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    setLoadingState(false);

                    // Handle specific error messages
                    String userFriendlyMessage;
                    if (error.contains("Invalid email or password")) {
                        userFriendlyMessage = "Invalid email or password. Please try again.";
                    } else if (error.contains("Network error")) {
                        userFriendlyMessage = "Network error. Please check your connection and try again.";
                    } else {
                        userFriendlyMessage = "Login failed. Please try again.";
                    }

                    Toast.makeText(LoginActivity.this, userFriendlyMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void setLoadingState(boolean isLoading) {
        if (binding.progressBar != null) {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
        binding.btnLogin.setEnabled(!isLoading);
        binding.etEmail.setEnabled(!isLoading);
        binding.etPassword.setEnabled(!isLoading);
        if (binding.tvSignup != null) {
            binding.tvSignup.setEnabled(!isLoading);
        }

        // Update button text to show loading state
        binding.btnLogin.setText(isLoading ? "Logging in..." : "Login");
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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
