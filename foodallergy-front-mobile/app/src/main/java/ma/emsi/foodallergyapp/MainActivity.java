package ma.emsi.foodallergyapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import ma.emsi.foodallergyapp.databinding.ActivityMainBinding;
import ma.emsi.foodallergyapp.ui.auth.LoginActivity;
import ma.emsi.foodallergyapp.utils.SessionManager;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private SessionManager sessionManager;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize session manager
        sessionManager = new SessionManager(this);

        // Check if user is logged in and session is valid
        if (!sessionManager.isLoggedIn() || !sessionManager.isSessionValid()) {
            redirectToLogin();
            return;
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        // Set up the bottom navigation
        BottomNavigationView navView = binding.navView;

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_scanner, R.id.nav_chat,
                R.id.nav_allergies, R.id.nav_profile)
                .build();

        // Get NavController from NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navView, navController);
        }

        // Log session debug info
        logSessionDebugInfo();
    }

    private void logSessionDebugInfo() {
        Log.d("SessionDebug", "User ID: " + sessionManager.getUserId());
        Log.d("SessionDebug", "User Email: " + sessionManager.getUserEmail());
        Log.d("SessionDebug", "User Name: " + sessionManager.getUserName());
        Log.d("SessionDebug", "Session valid: " + sessionManager.isSessionValid());
        Log.d("SessionDebug", "Allergies selected: " + sessionManager.areAllergiesSelected());
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_logout) {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        sessionManager.logoutUser();
        redirectToLogin();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController != null && NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh session when app comes to foreground
        if (sessionManager.isLoggedIn()) {
            sessionManager.refreshSession();
        }
    }
}
