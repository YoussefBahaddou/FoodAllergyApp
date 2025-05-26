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
import ma.emsi.foodallergyapp.databinding.ActivitySignupBinding;
import ma.emsi.foodallergyapp.utils.AuthManager;
import ma.emsi.foodallergyapp.utils.AuthManager.AuthCallback;

public class SignupActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authManager = AuthManager.getInstance(this);

        setupClickListeners();
    }

    private void setupClickListeners() {
        // Signup button click listener
        binding.btnSignup.setOnClickListener(v -> attemptSignup());

        // Login textview click listener
        binding.tvLogin.setOnClickListener(v -> {
            // Navigate back to login screen
            finish();
        });
    }

    private void attemptSignup() {
        // Clear any previous errors
        binding.tilUsername.setError(null);
        binding.tilEmail.setError(null);
        binding.tilPassword.setError(null);
        binding.tilConfirmPassword.setError(null);

        // Get input values
        String username = binding.etUsername.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();

        // Validate input
        boolean isValid = true;

        if (TextUtils.isEmpty(username)) {
            binding.tilUsername.setError(getString(R.string.error_empty_fields));
            isValid = false;
        }

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
        } else if (password.length() < 6) {
            binding.tilPassword.setError(getString(R.string.error_password_length));
            isValid = false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            binding.tilConfirmPassword.setError(getString(R.string.error_empty_fields));
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            binding.tilConfirmPassword.setError(getString(R.string.error_passwords_not_match));
            isValid = false;
        }

        if (!isValid) {
            return;
        }

        // Show loading indicator
        showLoading(true);

        // Attempt signup with our AuthManager
        authManager.signUp(email, password, username, new AuthCallback() {
            @Override
            public void onSuccess(AuthManager.MockUserInfo user) {
                showLoading(false);
                Toast.makeText(SignupActivity.this, "Inscription réussie!", Toast.LENGTH_SHORT).show();
                navigateToMainActivity();
                finish();
            }

            @Override
            public void onError(Exception e) {
                showLoading(false);
                Toast.makeText(SignupActivity.this, getString(R.string.error_auth_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void showLoading(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.btnSignup.setEnabled(!isLoading);
        binding.etUsername.setEnabled(!isLoading);
        binding.etEmail.setEnabled(!isLoading);
        binding.etPassword.setEnabled(!isLoading);
        binding.etConfirmPassword.setEnabled(!isLoading);
        binding.tvLogin.setEnabled(!isLoading);
    }
}
