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
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sporthub.R;
import com.example.sporthub.database.DatabaseHelper;
import com.example.sporthub.models.Event;
import com.example.sporthub.models.Match;
import com.example.sporthub.models.Team;
import com.example.sporthub.models.Tournament;
import com.example.sporthub.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private DatabaseHelper dbHelper;
    private TextView tvWelcome, tvUserRole;
    private LinearLayout layoutAdminDashboard, layoutPlayerDashboard;
    
    // State to hold the current export selection
    private String currentExportSelection = "";
    private ActivityResultLauncher<String> createDocumentLauncher;

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
        dbHelper = new DatabaseHelper(this);

        // Security check
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        // Initialize ActivityResultLauncher for SAF Export
        createDocumentLauncher = registerForActivityResult(
                new ActivityResultContracts.CreateDocument("text/csv"),
                uri -> {
                    if (uri != null) {
                        performCsvExport(uri, currentExportSelection);
                    } else {
                        Toast.makeText(this, "Export cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
        );

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

        // Toggle visibility based on Role
        if (sessionManager.isAdmin()) {
            tvUserRole.setText("Role: Admin");
            layoutAdminDashboard.setVisibility(View.VISIBLE);
            setupAdminListeners();
        } else if (sessionManager.isPlayer()) {
            tvUserRole.setText("Role: Player");
            layoutPlayerDashboard.setVisibility(View.VISIBLE);
            setupPlayerListeners();
        } else {
            Toast.makeText(this, "Session invalid. Please log in again.", Toast.LENGTH_SHORT).show();
            sessionManager.logoutUser();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
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
        findViewById(R.id.cardAdminExport).setOnClickListener(v -> {
            showExportDialog();
        });
    }

    private void showExportDialog() {
        if (!"Admin".equalsIgnoreCase(sessionManager.getRole())) {
            Toast.makeText(this, "Access Denied: Admins Only", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] options = {"Tournaments", "Teams", "Matches", "Events"};
        new AlertDialog.Builder(this)
                .setTitle("Select Data to Export")
                .setItems(options, (dialog, which) -> {
                    currentExportSelection = options[which];
                    // Launch SAF picker. The file name defaults to the selection.
                    String fileName = currentExportSelection.toLowerCase() + "_export.csv";
                    createDocumentLauncher.launch(fileName);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performCsvExport(android.net.Uri uri, String dataset) {
        if (!"Admin".equalsIgnoreCase(sessionManager.getRole())) {
            Toast.makeText(this, "Access Denied: Admins Only", Toast.LENGTH_SHORT).show();
            return;
        }

        try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
            if (outputStream == null) {
                Toast.makeText(this, "Unable to create file", Toast.LENGTH_SHORT).show();
                return;
            }

            StringBuilder csvData = new StringBuilder();

            switch (dataset) {
                case "Tournaments":
                    List<Tournament> tournaments = dbHelper.getAllTournaments();
                    csvData.append("ID,Name,Sport,Organizer,Start Date,End Date,Venue,Max Teams,Status\n");
                    for (Tournament t : tournaments) {
                        csvData.append(escapeCsv(String.valueOf(t.getId()))).append(",")
                                .append(escapeCsv(t.getName())).append(",")
                                .append(escapeCsv(t.getSport())).append(",")
                                .append(escapeCsv(t.getOrganizer())).append(",")
                                .append(escapeCsv(t.getStartDate())).append(",")
                                .append(escapeCsv(t.getEndDate())).append(",")
                                .append(escapeCsv(t.getVenue())).append(",")
                                .append(escapeCsv(String.valueOf(t.getMaxTeams()))).append(",")
                                .append(escapeCsv(t.getStatus())).append("\n");
                    }
                    break;
                case "Teams":
                    List<Team> teams = dbHelper.getAllTeams();
                    csvData.append("ID,Name,Sport,Captain,Contact Email,Tournament ID\n");
                    for (Team t : teams) {
                        csvData.append(escapeCsv(String.valueOf(t.getId()))).append(",")
                                .append(escapeCsv(t.getName())).append(",")
                                .append(escapeCsv(t.getSport())).append(",")
                                .append(escapeCsv(t.getCaptain())).append(",")
                                .append(escapeCsv(t.getContactEmail())).append(",")
                                .append(escapeCsv(String.valueOf(t.getTournamentId()))).append("\n");
                    }
                    break;
                case "Matches":
                    List<Match> matches = dbHelper.getAllMatches();
                    csvData.append("ID,Tournament ID,Tournament Name,Team A ID,Team A Name,Team B ID,Team B Name,Date,Venue,Status,Score A,Score B\n");
                    for (Match m : matches) {
                        csvData.append(escapeCsv(String.valueOf(m.getId()))).append(",")
                                .append(escapeCsv(String.valueOf(m.getTournamentId()))).append(",")
                                .append(escapeCsv(m.getTournamentName())).append(",")
                                .append(escapeCsv(String.valueOf(m.getTeamAId()))).append(",")
                                .append(escapeCsv(m.getTeamAName())).append(",")
                                .append(escapeCsv(String.valueOf(m.getTeamBId()))).append(",")
                                .append(escapeCsv(m.getTeamBName())).append(",")
                                .append(escapeCsv(m.getMatchDate())).append(",")
                                .append(escapeCsv(m.getVenue())).append(",")
                                .append(escapeCsv(m.getStatus())).append(",")
                                .append(escapeCsv(String.valueOf(m.getScoreA()))).append(",")
                                .append(escapeCsv(String.valueOf(m.getScoreB()))).append("\n");
                    }
                    break;
                case "Events":
                    List<Event> events = dbHelper.getAllEvents();
                    csvData.append("ID,Name,Description,Sport,Date,Venue,Organizer,Status\n");
                    for (Event e : events) {
                        csvData.append(escapeCsv(String.valueOf(e.getId()))).append(",")
                                .append(escapeCsv(e.getName())).append(",")
                                .append(escapeCsv(e.getDescription())).append(",")
                                .append(escapeCsv(e.getSport())).append(",")
                                .append(escapeCsv(e.getEventDate())).append(",")
                                .append(escapeCsv(e.getVenue())).append(",")
                                .append(escapeCsv(e.getOrganizer())).append(",")
                                .append(escapeCsv(e.getStatus())).append("\n");
                    }
                    break;
            }

            outputStream.write(csvData.toString().getBytes(StandardCharsets.UTF_8));
            Toast.makeText(this, dataset + " exported successfully", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(this, "Export failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        // If the value contains quotes, commas, or newlines, enclose in quotes and escape internal quotes
        if (value.contains("\"") || value.contains(",") || value.contains("\n") || value.contains("\r")) {
            value = value.replace("\"", "\"\"");
            return "\"" + value + "\"";
        }
        return value;
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
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}