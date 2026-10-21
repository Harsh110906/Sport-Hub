package com.example.sporthub.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sporthub.R;
import com.example.sporthub.database.DatabaseHelper;
import com.example.sporthub.models.Tournament;
import com.example.sporthub.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TournamentDetailActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private int tournamentId = -1;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.toolbarDetail), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbarDetail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Please log in to view tournament details", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        btnRegister = findViewById(R.id.btnRegister);

        if (getIntent().hasExtra("TOURNAMENT_ID")) {
            tournamentId = getIntent().getIntExtra("TOURNAMENT_ID", -1);
        }

        if (tournamentId != -1) {
            // Load will happen in onResume to keep state fresh
        } else {
            Toast.makeText(this, "Tournament Not Found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tournamentId != -1) {
            loadTournamentDetails();
        }
    }

    private void loadTournamentDetails() {
        Tournament t = dbHelper.getTournamentById(tournamentId);
        if (t != null) {
            TextView tvName = findViewById(R.id.tvDetName);
            TextView tvStatus = findViewById(R.id.tvDetStatus);
            TextView tvSport = findViewById(R.id.tvDetSport);
            TextView tvDates = findViewById(R.id.tvDetDates);
            TextView tvVenue = findViewById(R.id.tvDetVenue);
            TextView tvOrganizer = findViewById(R.id.tvDetOrganizer);
            TextView tvMaxTeams = findViewById(R.id.tvDetMaxTeams);

            tvName.setText(t.getName());
            tvStatus.setText(t.getStatus());
            tvSport.setText(t.getSport());
            tvDates.setText(t.getStartDate() + " to " + t.getEndDate());
            
            String venue = (t.getVenue() == null || t.getVenue().isEmpty()) ? "Not specified" : t.getVenue();
            tvVenue.setText(venue);
            
            String organizer = (t.getOrganizer() == null || t.getOrganizer().isEmpty()) ? "Not specified" : t.getOrganizer();
            tvOrganizer.setText(organizer);
            
            tvMaxTeams.setText(String.valueOf(t.getMaxTeams()) + " Teams");

            setupRegistrationButton(t.getStatus());

        } else {
            Toast.makeText(this, "Tournament Error", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupRegistrationButton(String status) {
        String role = sessionManager.getRole();
        String uid = sessionManager.getFirebaseUid();

        // 1. Hide for Admins or invalid sessions
        if ("Admin".equalsIgnoreCase(role) || uid == null) {
            btnRegister.setVisibility(android.view.View.GONE);
            return;
        }

        // 2. Visible for Players
        btnRegister.setVisibility(android.view.View.VISIBLE);

        // 3. Check if already registered
        boolean isRegistered = dbHelper.isPlayerRegistered(uid, tournamentId);
        
        // 4. Check tournament status
        boolean isUpcoming = "Upcoming".equalsIgnoreCase(status);

        if (isRegistered) {
            if (isUpcoming) {
                btnRegister.setEnabled(true);
                btnRegister.setText("Cancel Registration");
                btnRegister.setOnClickListener(v -> confirmCancellation(uid));
            } else {
                btnRegister.setEnabled(false);
                btnRegister.setText("Registered (In Progress)");
            }
            return;
        }

        if (!isUpcoming) {
            btnRegister.setEnabled(false);
            btnRegister.setText("Registration Closed");
            return;
        }

        // 5. Setup active registration click
        btnRegister.setEnabled(true);
        btnRegister.setText("Register");
        btnRegister.setOnClickListener(v -> processRegistration(uid));
    }

    private void confirmCancellation(String uid) {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Registration")
                .setMessage("Are you sure you want to cancel your registration for this tournament?")
                .setPositiveButton("Yes", (dialog, which) -> processCancellation(uid))
                .setNegativeButton("No", null)
                .show();
    }

    private void processCancellation(String uid) {
        btnRegister.setEnabled(false);
        btnRegister.setText("Cancelling...");

        int result = dbHelper.cancelTournamentRegistration(uid, tournamentId);

        switch (result) {
            case DatabaseHelper.CANCEL_SUCCESS:
                Toast.makeText(this, "Registration Cancelled.", Toast.LENGTH_SHORT).show();
                btnRegister.setEnabled(true);
                btnRegister.setText("Register");
                btnRegister.setOnClickListener(v -> processRegistration(uid));
                break;
            case DatabaseHelper.CANCEL_NOT_FOUND:
                Toast.makeText(this, "Registration not found or already cancelled.", Toast.LENGTH_SHORT).show();
                loadTournamentDetails(); // Refresh to resync state
                break;
            case DatabaseHelper.CANCEL_INELIGIBLE:
                Toast.makeText(this, "Cannot cancel. Tournament is no longer upcoming.", Toast.LENGTH_SHORT).show();
                loadTournamentDetails(); // Refresh to resync state
                break;
            case DatabaseHelper.CANCEL_ERROR:
            default:
                Toast.makeText(this, "Error cancelling registration.", Toast.LENGTH_SHORT).show();
                btnRegister.setEnabled(true);
                btnRegister.setText("Cancel Registration");
                break;
        }
    }

    private void processRegistration(String uid) {
        btnRegister.setEnabled(false);
        btnRegister.setText("Processing...");

        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());

        int result = dbHelper.registerPlayerForTournament(uid, tournamentId, currentDate);

        switch (result) {
            case DatabaseHelper.REGISTRATION_SUCCESS:
                Toast.makeText(this, "Successfully Registered!", Toast.LENGTH_SHORT).show();
                btnRegister.setText("Already Registered");
                break;
            case DatabaseHelper.REGISTRATION_DUPLICATE:
                Toast.makeText(this, "You are already registered.", Toast.LENGTH_SHORT).show();
                btnRegister.setText("Already Registered");
                break;
            case DatabaseHelper.REGISTRATION_INELIGIBLE:
                Toast.makeText(this, "This tournament is not open for registration.", Toast.LENGTH_SHORT).show();
                btnRegister.setText("Registration Closed");
                break;
            case DatabaseHelper.REGISTRATION_ERROR:
            default:
                Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                btnRegister.setEnabled(true);
                btnRegister.setText("Register");
                break;
        }
    }
}