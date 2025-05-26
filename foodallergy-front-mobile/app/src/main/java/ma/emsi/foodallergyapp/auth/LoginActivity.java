package ma.emsi.foodallergyapp.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ma.emsi.foodallergyapp.MainActivity;
import ma.emsi.foodallergyapp.R;
import ma.emsi.foodallergyapp.databinding.ActivityLoginBinding;
import ma.emsi.foodallergyapp.utils.AuthManager;
import ma.emsi.foodallergyapp.utils.AuthManager.AuthCallback;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authManager = AuthManager.getInstance(this);

        // Check if user is already authenticated
        if (authManager.isAuthenticated()) {
            navigateToMainActivity();
            finish();
        }

        setupClickListeners();
    }

    private void setupClickListeners() {
        // Login button click listener
        binding.btnLogin.setOnClickListener(v -> attemptLogin());

        // Register textview click listener
        binding.tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        // Forgot password click listener
        binding.tvForgotPassword.setOnClickListener(v -> {
            // TODO: Implement forgot password functionality
            Toast.makeText(LoginActivity.this, "Fonctionnalité à venir", Toast.LENGTH_SHORT).show();
        });
    }

    private void attemptLogin() {
        // Clear any previous errors
        binding.tilEmail.setError(null);
        binding.tilPassword.setError(null);

        // Get input values
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        // Validate input
        boolean isValid = true;

        if (TextUtils.isEmpty(email)) {
            binding.tilEmail.setError(getString(R.string.error_empty_fields));
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.setError(getString(R.string.error_invalid_email));
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            binding.tilPassword.setError(getString(R.string.error_empty_fields));
            isValid = false;
        }

        if (!isValid) {
            return;
        }

        // Show loading indicator
        showLoading(true);

        // Attempt login with our AuthManager
        authManager.signIn(email, password, new AuthCallback() {
            @Override
            public void onSuccess(AuthManager.MockUserInfo user) {
                showLoading(false);
                Toast.makeText(LoginActivity.this, "Connexion réussie!", Toast.LENGTH_SHORT).show();
                navigateToMainActivity();
                finish();
            }

            @Override
            public void onError(Exception e) {
                showLoading(false);
                Toast.makeText(LoginActivity.this, getString(R.string.error_auth_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void showLoading(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.btnLogin.setEnabled(!isLoading);
        binding.etEmail.setEnabled(!isLoading);
        binding.etPassword.setEnabled(!isLoading);
        binding.tvRegister.setEnabled(!isLoading);
        binding.tvForgotPassword.setEnabled(!isLoading);
    }
}
