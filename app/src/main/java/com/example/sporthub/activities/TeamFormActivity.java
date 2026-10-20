package com.example.sporthub.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sporthub.R;
import com.example.sporthub.database.DatabaseHelper;
import com.example.sporthub.models.Team;
import com.example.sporthub.models.Tournament;
import com.example.sporthub.utils.SessionManager;

import java.util.List;

public class TeamFormActivity extends AppCompatActivity {

    private EditText etName, etSport, etCaptain, etEmail;
    private Spinner spnTournaments;
    private Button btnSave, btnDelete;
    private DatabaseHelper dbHelper;
    private List<Tournament> tournamentList;
    private int teamId = -1; // -1 means Create Mode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_form);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.toolbarTeamForm), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        // Security check
        SessionManager sessionManager = new SessionManager(this);
        if (!"Admin".equalsIgnoreCase(sessionManager.getRole())) {
            Toast.makeText(this, "Access Denied", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbarTeamForm);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        dbHelper = new DatabaseHelper(this);

        etName = findViewById(R.id.etTeamName);
        etSport = findViewById(R.id.etTeamSport);
        etCaptain = findViewById(R.id.etTeamCaptain);
        etEmail = findViewById(R.id.etTeamEmail);
        spnTournaments = findViewById(R.id.spnTournaments);
        btnSave = findViewById(R.id.btnSaveTeam);
        btnDelete = findViewById(R.id.btnDeleteTeam);

        loadTournamentsIntoSpinner();

        if (getIntent().hasExtra("TEAM_ID")) {
            teamId = getIntent().getIntExtra("TEAM_ID", -1);
        }

        if (teamId != -1) {
            getSupportActionBar().setTitle("Edit Team");
            btnDelete.setVisibility(View.VISIBLE);
            loadTeamData();
        } else {
            getSupportActionBar().setTitle("Create Team");
        }

        btnSave.setOnClickListener(v -> saveTeam());
        btnDelete.setOnClickListener(v -> confirmDelete());
    }

    private void loadTournamentsIntoSpinner() {
        tournamentList = dbHelper.getAllTournaments();
        if (tournamentList.isEmpty()) {
            Toast.makeText(this, "You must create a Tournament first!", Toast.LENGTH_LONG).show();
            btnSave.setEnabled(false);
            return;
        }
        ArrayAdapter<Tournament> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tournamentList);
        spnTournaments.setAdapter(adapter);
    }

    private void loadTeamData() {
        List<Team> allTeams = dbHelper.getAllTeams();
        Team teamToEdit = null;
        for (Team t : allTeams) {
            if (t.getId() == teamId) {
                teamToEdit = t;
                break;
            }
        }

        if (teamToEdit != null) {
            etName.setText(teamToEdit.getName());
            etSport.setText(teamToEdit.getSport());
            etCaptain.setText(teamToEdit.getCaptain());
            etEmail.setText(teamToEdit.getContactEmail());

            for (int i = 0; i < tournamentList.size(); i++) {
                if (tournamentList.get(i).getId() == teamToEdit.getTournamentId()) {
                    spnTournaments.setSelection(i);
                    break;
                }
            }
        } else {
            Toast.makeText(this, "Team not found.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void saveTeam() {
        String name = etName.getText().toString().trim();
        String sport = etSport.getText().toString().trim();
        String captain = etCaptain.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(sport) || TextUtils.isEmpty(captain) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email address");
            return;
        }

        if (spnTournaments.getSelectedItem() == null) {
            Toast.makeText(this, "Please select a tournament.", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSave.setEnabled(false);
        btnSave.setText("Saving...");

        Tournament selectedTournament = (Tournament) spnTournaments.getSelectedItem();

        Team t = new Team();
        t.setName(name);
        t.setSport(sport);
        t.setCaptain(captain);
        t.setContactEmail(email);
        t.setTournamentId(selectedTournament.getId());

        if (teamId == -1) {
            long result = dbHelper.addTeam(t);
            if (result != -1) {
                Toast.makeText(this, "Team Created", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error creating team", Toast.LENGTH_SHORT).show();
                btnSave.setEnabled(true);
                btnSave.setText("Save");
            }
        } else {
            t.setId(teamId);
            int rows = dbHelper.updateTeam(t);
            if (rows > 0) {
                Toast.makeText(this, "Team Updated", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error updating team", Toast.LENGTH_SHORT).show();
                btnSave.setEnabled(true);
                btnSave.setText("Save");
            }
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Team")
                .setMessage("Are you sure you want to permanently delete this team?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dbHelper.deleteTeam(teamId);
                    Toast.makeText(this, "Team Deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }
}