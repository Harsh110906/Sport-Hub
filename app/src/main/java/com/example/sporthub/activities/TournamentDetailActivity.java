package com.example.sporthub.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
            tvSport.setText("Sport: " + t.getSport());
            tvDates.setText("Dates: " + t.getStartDate() + " to " + t.getEndDate());
            
            String venue = (t.getVenue() == null || t.getVenue().isEmpty()) ? "TBD" : t.getVenue();
            tvVenue.setText("Venue: " + venue);
            
            String organizer = (t.getOrganizer() == null || t.getOrganizer().isEmpty()) ? "TBD" : t.getOrganizer();
            tvOrganizer.setText("Organizer: " + organizer);
            
            tvMaxTeams.setText("Max Teams Capacity: " + t.getMaxTeams());

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
        if (isRegistered) {
            btnRegister.setEnabled(false);
            btnRegister.setText("Already Registered");
            return;
        }

        // 4. Check if tournament is upcoming
        if (!"Upcoming".equalsIgnoreCase(status)) {
            btnRegister.setEnabled(false);
            btnRegister.setText("Registration Closed");
            return;
        }

        // 5. Setup active registration click
        btnRegister.setEnabled(true);
        btnRegister.setText("Register");
        btnRegister.setOnClickListener(v -> processRegistration(uid));
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