package com.example.sporthub.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.example.sporthub.models.Match;
import com.example.sporthub.models.Team;
import com.example.sporthub.models.Tournament;
import com.example.sporthub.utils.SessionManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MatchFormActivity extends AppCompatActivity {

    private Spinner spnTournament, spnTeamA, spnTeamB, spnStatus;
    private TextView tvDate;
    private EditText etVenue, etScoreA, etScoreB;
    private LinearLayout layoutScores;
    private Button btnSave, btnDelete;
    
    private DatabaseHelper dbHelper;
    private List<Tournament> tournamentList;
    private List<Team> currentTeamList = new ArrayList<>();
    private int matchId = -1; // -1 = Create mode
    private Match matchToEdit = null;
    private boolean isInitialLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_form);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.toolbarMatchForm), (v, insets) -> {
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

        Toolbar toolbar = findViewById(R.id.toolbarMatchForm);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        dbHelper = new DatabaseHelper(this);

        spnTournament = findViewById(R.id.spnMatchTournament);
        spnTeamA = findViewById(R.id.spnTeamA);
        spnTeamB = findViewById(R.id.spnTeamB);
        spnStatus = findViewById(R.id.spnMatchStatus);
        tvDate = findViewById(R.id.tvMatchDate);
        etVenue = findViewById(R.id.etMatchVenue);
        etScoreA = findViewById(R.id.etScoreA);
        etScoreB = findViewById(R.id.etScoreB);
        layoutScores = findViewById(R.id.layoutScores);
        btnSave = findViewById(R.id.btnSaveMatch);
        btnDelete = findViewById(R.id.btnDeleteMatch);

        // Status Spinner
        String[] statuses = {"Scheduled", "Ongoing", "Completed"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, statuses);
        spnStatus.setAdapter(statusAdapter);

        // Toggle Score visibility
        spnStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if ("Completed".equalsIgnoreCase(statuses[position])) {
                    layoutScores.setVisibility(View.VISIBLE);
                } else {
                    layoutScores.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        tvDate.setOnClickListener(v -> showDatePicker(tvDate));

        btnSave.setOnClickListener(v -> saveMatch());
        btnDelete.setOnClickListener(v -> confirmDelete());

        if (getIntent().hasExtra("MATCH_ID")) {
            matchId = getIntent().getIntExtra("MATCH_ID", -1);
        }

        if (matchId != -1) {
            getSupportActionBar().setTitle("Edit Match");
            btnDelete.setVisibility(View.VISIBLE);
            loadMatchToEdit();
        } else {
            getSupportActionBar().setTitle("Schedule Match");
        }

        loadTournamentsIntoSpinner();
    }

    private void showDatePicker(TextView target) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String dateStr = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            target.setText(dateStr);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void loadMatchToEdit() {
        List<Match> allMatches = dbHelper.getAllMatches();
        for (Match m : allMatches) {
            if (m.getId() == matchId) {
                matchToEdit = m;
                break;
            }
        }
        if (matchToEdit == null) {
            Toast.makeText(this, "Match not found.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadTournamentsIntoSpinner() {
        tournamentList = dbHelper.getAllTournaments();
        if (tournamentList.isEmpty()) {
            Toast.makeText(this, "No tournaments found. Create one first.", Toast.LENGTH_LONG).show();
            btnSave.setEnabled(false);
            return;
        }

        ArrayAdapter<Tournament> tAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tournamentList);
        spnTournament.setAdapter(tAdapter);

        // Dependent Spinner logic
        spnTournament.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Tournament selected = tournamentList.get(position);
                loadTeamsForTournament(selected.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Prepopulate if editing
        if (matchToEdit != null && isInitialLoad) {
            for (int i = 0; i < tournamentList.size(); i++) {
                if (tournamentList.get(i).getId() == matchToEdit.getTournamentId()) {
                    spnTournament.setSelection(i);
                    break;
                }
            }
        }
    }

    private void loadTeamsForTournament(int tournamentId) {
        currentTeamList = dbHelper.getTeamsByTournamentId(tournamentId);
        if (currentTeamList.size() < 2) {
            Toast.makeText(this, "Tournament needs at least 2 teams to schedule a match.", Toast.LENGTH_SHORT).show();
            btnSave.setEnabled(false);
        } else {
            btnSave.setEnabled(true);
        }

        ArrayAdapter<Team> teamAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, currentTeamList);
        spnTeamA.setAdapter(teamAdapter);
        spnTeamB.setAdapter(teamAdapter);

        if (matchToEdit != null && isInitialLoad) {
            for (int i = 0; i < currentTeamList.size(); i++) {
                if (currentTeamList.get(i).getId() == matchToEdit.getTeamAId()) spnTeamA.setSelection(i);
                if (currentTeamList.get(i).getId() == matchToEdit.getTeamBId()) spnTeamB.setSelection(i);
            }
            tvDate.setText(matchToEdit.getMatchDate());
            etVenue.setText(matchToEdit.getVenue());
            
            for (int i = 0; i < spnStatus.getCount(); i++) {
                if (spnStatus.getItemAtPosition(i).toString().equalsIgnoreCase(matchToEdit.getStatus())) {
                    spnStatus.setSelection(i);
                    break;
                }
            }
            
            if ("Completed".equalsIgnoreCase(matchToEdit.getStatus())) {
                etScoreA.setText(String.valueOf(matchToEdit.getScoreA()));
                etScoreB.setText(String.valueOf(matchToEdit.getScoreB()));
            }

            isInitialLoad = false;
        }
    }

    private void saveMatch() {
        if (spnTournament.getSelectedItem() == null || currentTeamList.size() < 2) {
            Toast.makeText(this, "Cannot save. Missing tournament or teams.", Toast.LENGTH_SHORT).show();
            return;
        }

        Tournament selectedTournament = (Tournament) spnTournament.getSelectedItem();
        Team teamA = (Team) spnTeamA.getSelectedItem();
        Team teamB = (Team) spnTeamB.getSelectedItem();

        if (teamA.getId() == teamB.getId()) {
            Toast.makeText(this, "Team A and Team B must be distinct.", Toast.LENGTH_SHORT).show();
            return;
        }

        String date = tvDate.getText().toString().trim();
        String venue = etVenue.getText().toString().trim();
        String status = spnStatus.getSelectedItem().toString();

        if (TextUtils.isEmpty(date) || TextUtils.isEmpty(venue)) {
            Toast.makeText(this, "Date and Venue are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        int scoreA = 0;
        int scoreB = 0;
        if ("Completed".equalsIgnoreCase(status)) {
            try {
                scoreA = Integer.parseInt(etScoreA.getText().toString().trim());
                scoreB = Integer.parseInt(etScoreB.getText().toString().trim());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Scores must be valid numbers for Completed matches.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        btnSave.setEnabled(false);
        btnSave.setText("Saving...");

        Match m = new Match();
        m.setTournamentId(selectedTournament.getId());
        m.setTeamAId(teamA.getId());
        m.setTeamBId(teamB.getId());
        m.setMatchDate(date);
        m.setVenue(venue);
        m.setStatus(status);
        m.setScoreA(scoreA);
        m.setScoreB(scoreB);

        if (matchId == -1) {
            long result = dbHelper.addMatch(m);
            if (result != -1) {
                Toast.makeText(this, "Match Scheduled", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error scheduling match", Toast.LENGTH_SHORT).show();
                btnSave.setEnabled(true);
                btnSave.setText("Save");
            }
        } else {
            m.setId(matchId);
            int rows = dbHelper.updateMatch(m);
            if (rows > 0) {
                Toast.makeText(this, "Match Updated", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error updating match", Toast.LENGTH_SHORT).show();
                btnSave.setEnabled(true);
                btnSave.setText("Save");
            }
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Match")
                .setMessage("Are you sure you want to delete this match?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dbHelper.deleteMatch(matchId);
                    Toast.makeText(this, "Match Deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }
}