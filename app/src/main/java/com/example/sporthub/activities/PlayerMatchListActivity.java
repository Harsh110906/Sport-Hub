package com.example.sporthub.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sporthub.R;
import com.example.sporthub.adapters.MatchAdapter;
import com.example.sporthub.database.DatabaseHelper;
import com.example.sporthub.models.Match;
import com.example.sporthub.utils.SessionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlayerMatchListActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private ListView lvMatches;
    private TextView tvEmptyState;
    private MatchAdapter adapter;
    private List<Match> masterMatchList;
    private List<Match> filteredMatchList;
    
    private Spinner spnTournamentFilter;
    private Spinner spnStatusFilter;
    private Spinner spnSortMatches;
    private Button btnClearFilters;
    
    private boolean isSpinnerInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_match_list);

        SessionManager sessionManager = new SessionManager(this);
        if (!sessionManager.isPlayer()) {
            android.widget.Toast.makeText(this, "Access Denied: Player access required", android.widget.Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.toolbarPlayerMatches), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbarPlayerMatches);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        dbHelper = new DatabaseHelper(this);
        lvMatches = findViewById(R.id.lvPlayerMatches);
        tvEmptyState = findViewById(R.id.tvPlayerMatchEmptyState);
        spnTournamentFilter = findViewById(R.id.spnPlayerTournamentFilter);
        spnStatusFilter = findViewById(R.id.spnPlayerStatusFilter);
        spnSortMatches = findViewById(R.id.spnPlayerSortMatches);
        btnClearFilters = findViewById(R.id.btnPlayerClearFilters);

        // Setup static status spinner
        String[] statuses = {"All Statuses", "Scheduled", "Ongoing", "Completed"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, statuses);
        spnStatusFilter.setAdapter(statusAdapter);

        // Setup static sort spinner
        String[] sortOptions = {"Latest First", "Earliest First"};
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sortOptions);
        spnSortMatches.setAdapter(sortAdapter);

        setupFilterListeners();
    }

    private void setupFilterListeners() {
        AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isSpinnerInitialized) {
                    applyFilters();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        spnTournamentFilter.setOnItemSelectedListener(spinnerListener);
        spnStatusFilter.setOnItemSelectedListener(spinnerListener);
        spnSortMatches.setOnItemSelectedListener(spinnerListener);

        btnClearFilters.setOnClickListener(v -> {
            isSpinnerInitialized = false;
            spnTournamentFilter.setSelection(0);
            spnStatusFilter.setSelection(0);
            spnSortMatches.setSelection(0);
            isSpinnerInitialized = true;
            applyFilters();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMatches();
    }

    private void loadMatches() {
        masterMatchList = dbHelper.getAllMatches();
        
        // Dynamically populate Tournament Spinner based on available data
        Set<String> tournaments = new HashSet<>();
        for (Match m : masterMatchList) {
            if (m.getTournamentName() != null && !m.getTournamentName().trim().isEmpty()) {
                tournaments.add(m.getTournamentName().trim());
            }
        }
        List<String> tournamentOptions = new ArrayList<>();
        tournamentOptions.add("All Tournaments");
        tournamentOptions.addAll(tournaments);
        
        // Preserve selection if possible
        String currentSelection = null;
        if (spnTournamentFilter.getSelectedItem() != null) {
            currentSelection = spnTournamentFilter.getSelectedItem().toString();
        }

        isSpinnerInitialized = false;
        ArrayAdapter<String> tAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tournamentOptions);
        spnTournamentFilter.setAdapter(tAdapter);
        
        if (currentSelection != null && tournamentOptions.contains(currentSelection)) {
            spnTournamentFilter.setSelection(tournamentOptions.indexOf(currentSelection));
        } else {
            spnTournamentFilter.setSelection(0);
        }
        isSpinnerInitialized = true;

        if (adapter == null) {
            filteredMatchList = new ArrayList<>(masterMatchList);
            adapter = new MatchAdapter(this, filteredMatchList);
            lvMatches.setAdapter(adapter);
        }
        
        applyFilters();
    }

    private void applyFilters() {
        if (masterMatchList == null) return;

        String selectedTournament = spnTournamentFilter.getSelectedItem() != null ? spnTournamentFilter.getSelectedItem().toString() : "All Tournaments";
        String selectedStatus = spnStatusFilter.getSelectedItem() != null ? spnStatusFilter.getSelectedItem().toString() : "All Statuses";
        String selectedSort = spnSortMatches.getSelectedItem() != null ? spnSortMatches.getSelectedItem().toString() : "Latest First";

        filteredMatchList = new ArrayList<>();

        for (Match m : masterMatchList) {
            boolean matchesTournament = true;
            boolean matchesStatus = true;

            if (!"All Tournaments".equals(selectedTournament)) {
                matchesTournament = m.getTournamentName() != null && m.getTournamentName().trim().equalsIgnoreCase(selectedTournament);
            }

            if (!"All Statuses".equals(selectedStatus)) {
                matchesStatus = m.getStatus() != null && m.getStatus().equalsIgnoreCase(selectedStatus);
            }

            if (matchesTournament && matchesStatus) {
                filteredMatchList.add(m);
            }
        }
        
        // Sorting logic based on date string "yyyy-MM-dd"
        Collections.sort(filteredMatchList, new Comparator<Match>() {
            @Override
            public int compare(Match m1, Match m2) {
                String d1 = m1.getMatchDate() != null ? m1.getMatchDate() : "";
                String d2 = m2.getMatchDate() != null ? m2.getMatchDate() : "";
                if ("Earliest First".equals(selectedSort)) {
                    return d1.compareTo(d2); // ASC
                } else {
                    return d2.compareTo(d1); // DESC
                }
            }
        });

        if (filteredMatchList.isEmpty()) {
            if (masterMatchList.isEmpty()) {
                tvEmptyState.setText("No matches scheduled right now.");
            } else {
                tvEmptyState.setText("No matches match your filters.");
            }
            tvEmptyState.setVisibility(View.VISIBLE);
            lvMatches.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            lvMatches.setVisibility(View.VISIBLE);
        }

        if (adapter != null) {
            adapter.updateData(filteredMatchList);
        }
    }
}