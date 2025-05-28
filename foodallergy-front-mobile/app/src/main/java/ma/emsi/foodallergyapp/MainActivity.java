package ma.emsi.foodallergyapp;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import ma.emsi.foodallergyapp.databinding.ActivityMainBinding;
import ma.emsi.foodallergyapp.utils.SessionManager;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private static final String TAG = "MainActivity";
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);

        // Debug session state when app starts
        debugSessionState();

        // Set up the toolbar
        setSupportActionBar(binding.toolbar);

        // Setup bottom navigation
        BottomNavigationView navView = binding.navView;

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_scanner,
                R.id.navigation_allergies, R.id.navigation_profile)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Debug session state when returning to main activity
        debugSessionState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_logout) {
            handleLogout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void handleLogout() {
        Snackbar.make(binding.getRoot(), getString(R.string.success_logout), Snackbar.LENGTH_LONG).show();
    }

    // Add this method to verify login state
    private void debugSessionState() {
        Log.d("SessionDebug", "=== SESSION DEBUG INFO ===");
        Log.d("SessionDebug", "Is logged in: " + sessionManager.isLoggedIn());
        Log.d("SessionDebug", "User ID: " + sessionManager.getUserId());
        Log.d("SessionDebug", "User Email: " + sessionManager.getUserEmail());
        Log.d("SessionDebug", "User Name: " + sessionManager.getUserName());
        Log.d("SessionDebug", "Session valid: " + sessionManager.isSessionValid());
        Log.d("SessionDebug", "Allergies selected: " + sessionManager.areAllergiesSelected());
        Log.d("SessionDebug", "========================");
    }
}
