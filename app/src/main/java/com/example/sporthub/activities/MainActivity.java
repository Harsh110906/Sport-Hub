package com.example.sporthub.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sporthub.R;
import com.example.sporthub.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private TextView tvWelcome, tvUserRole;
    private LinearLayout layoutAdminDashboard, layoutPlayerDashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Handle window insets so toolbar isn't hidden under status bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sessionManager = new SessionManager(this);

        // Security check
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize UI components
        tvWelcome = findViewById(R.id.tvWelcome);
        tvUserRole = findViewById(R.id.tvUserRole);
        layoutAdminDashboard = findViewById(R.id.layoutAdminDashboard);
        layoutPlayerDashboard = findViewById(R.id.layoutPlayerDashboard);

        // Populate Profile Info safely
        String userName = sessionManager.getName();
        if (userName == null || userName.isEmpty()) {
            userName = "User";
        }
        tvWelcome.setText("Welcome, " + userName + "!");

        String role = sessionManager.getRole();
        if (role == null || role.isEmpty()) {
            role = "Player"; // Safe fallback
        }
        tvUserRole.setText("Role: " + role);

        // Toggle visibility based on Role
        if ("Admin".equalsIgnoreCase(role)) {
            layoutAdminDashboard.setVisibility(View.VISIBLE);
            setupAdminListeners();
        } else {
            layoutPlayerDashboard.setVisibility(View.VISIBLE);
            setupPlayerListeners();
        }

        // Shared Profile listener
        findViewById(R.id.cardSharedProfile).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        });
    }

    private void setupAdminListeners() {
        findViewById(R.id.cardAdminTournaments).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AdminTournamentListActivity.class));
        });
        findViewById(R.id.cardAdminTeams).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AdminTeamListActivity.class));
        });
        findViewById(R.id.cardAdminMatches).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AdminMatchListActivity.class));
        });
        findViewById(R.id.cardAdminEvents).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AdminEventListActivity.class));
        });
    }

    private void setupPlayerListeners() {
        findViewById(R.id.cardPlayerTournaments).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, PlayerTournamentListActivity.class));
        });
        findViewById(R.id.cardPlayerMatches).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, PlayerMatchListActivity.class));
        });
        findViewById(R.id.cardPlayerEvents).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, PlayerEventListActivity.class));
        });
        findViewById(R.id.cardPlayerRegistrations).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, PlayerRegistrationsActivity.class));
        });
        findViewById(R.id.cardPlayerFitness).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, PlayerFitnessListActivity.class));
        });
        findViewById(R.id.cardPlayerWorkout).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, PlayerWorkoutListActivity.class));
        });
    }

    private void showComingSoonDialog(String moduleName) {
        new AlertDialog.Builder(this)
                .setTitle(moduleName)
                .setMessage("This feature is coming in a later phase.")
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            performLogout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void performLogout() {
        FirebaseAuth.getInstance().signOut();
        sessionManager.logoutUser();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }
}