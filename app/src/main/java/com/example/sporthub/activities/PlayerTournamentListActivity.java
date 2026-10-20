package com.example.sporthub.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sporthub.R;
import com.example.sporthub.adapters.TournamentAdapter;
import com.example.sporthub.database.DatabaseHelper;
import com.example.sporthub.models.Tournament;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlayerTournamentListActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private ListView lvTournaments;
    private TextView tvEmptyState;
    private TournamentAdapter adapter;
    private List<Tournament> masterTournamentList;
    private List<Tournament> filteredTournamentList;
    
    private EditText etSearch;
    private Spinner spnSportFilter;
    private Spinner spnStatusFilter;
    private Button btnClearFilters;
    
    private boolean isSpinnerInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_tournament_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.toolbarPlayerList), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbarPlayerList);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        dbHelper = new DatabaseHelper(this);
        lvTournaments = findViewById(R.id.lvPlayerTournaments);
        tvEmptyState = findViewById(R.id.tvPlayerEmptyState);
        etSearch = findViewById(R.id.etPlayerSearch);
        spnSportFilter = findViewById(R.id.spnPlayerSportFilter);
        spnStatusFilter = findViewById(R.id.spnPlayerStatusFilter);
        btnClearFilters = findViewById(R.id.btnPlayerClearFilters);

        // Setup static status spinner
        String[] statuses = {"All Statuses", "Upcoming", "Ongoing", "Completed"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, statuses);
        spnStatusFilter.setAdapter(statusAdapter);

        lvTournaments.setOnItemClickListener((parent, view, position, id) -> {
            Tournament selected = filteredTournamentList.get(position);
            Intent intent = new Intent(PlayerTournamentListActivity.this, TournamentDetailActivity.class);
            intent.putExtra("TOURNAMENT_ID", selected.getId());
            startActivity(intent);
        });

        setupFilterListeners();
    }

    private void setupFilterListeners() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

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

        spnSportFilter.setOnItemSelectedListener(spinnerListener);
        spnStatusFilter.setOnItemSelectedListener(spinnerListener);

        btnClearFilters.setOnClickListener(v -> {
            isSpinnerInitialized = false;
            etSearch.setText("");
            spnSportFilter.setSelection(0);
            spnStatusFilter.setSelection(0);
            isSpinnerInitialized = true;
            applyFilters();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTournaments();
    }

    private void loadTournaments() {
        masterTournamentList = dbHelper.getAllTournaments();
        
        // Dynamically populate Sport Spinner based on available data
        Set<String> sports = new HashSet<>();
        for (Tournament t : masterTournamentList) {
            if (t.getSport() != null && !t.getSport().trim().isEmpty()) {
                sports.add(t.getSport().trim());
            }
        }
        List<String> sportOptions = new ArrayList<>();
        sportOptions.add("All Sports");
        sportOptions.addAll(sports);
        
        // Preserve selection if possible
        String currentSportSelection = null;
        if (spnSportFilter.getSelectedItem() != null) {
            currentSportSelection = spnSportFilter.getSelectedItem().toString();
        }

        isSpinnerInitialized = false;
        ArrayAdapter<String> sportAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sportOptions);
        spnSportFilter.setAdapter(sportAdapter);
        
        if (currentSportSelection != null && sportOptions.contains(currentSportSelection)) {
            spnSportFilter.setSelection(sportOptions.indexOf(currentSportSelection));
        } else {
            spnSportFilter.setSelection(0);
        }
        isSpinnerInitialized = true;

        if (adapter == null) {
            filteredTournamentList = new ArrayList<>(masterTournamentList);
            adapter = new TournamentAdapter(this, filteredTournamentList);
            lvTournaments.setAdapter(adapter);
        }
        
        applyFilters();
    }

    private void applyFilters() {
        if (masterTournamentList == null) return;

        String searchQuery = etSearch.getText().toString().trim().toLowerCase();
        String selectedSport = spnSportFilter.getSelectedItem() != null ? spnSportFilter.getSelectedItem().toString() : "All Sports";
        String selectedStatus = spnStatusFilter.getSelectedItem() != null ? spnStatusFilter.getSelectedItem().toString() : "All Statuses";

        filteredTournamentList = new ArrayList<>();

        for (Tournament t : masterTournamentList) {
            boolean matchesName = true;
            boolean matchesSport = true;
            boolean matchesStatus = true;

            if (!searchQuery.isEmpty()) {
                matchesName = t.getName() != null && t.getName().toLowerCase().contains(searchQuery);
            }

            if (!"All Sports".equals(selectedSport)) {
                matchesSport = t.getSport() != null && t.getSport().trim().equalsIgnoreCase(selectedSport);
            }

            if (!"All Statuses".equals(selectedStatus)) {
                matchesStatus = t.getStatus() != null && t.getStatus().equalsIgnoreCase(selectedStatus);
            }

            if (matchesName && matchesSport && matchesStatus) {
                filteredTournamentList.add(t);
            }
        }

        if (filteredTournamentList.isEmpty()) {
            if (masterTournamentList.isEmpty()) {
                tvEmptyState.setText("No tournaments available right now.");
            } else {
                tvEmptyState.setText("No tournaments match your filters.");
            }
            tvEmptyState.setVisibility(View.VISIBLE);
            lvTournaments.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            lvTournaments.setVisibility(View.VISIBLE);
        }

        if (adapter != null) {
            adapter.updateData(filteredTournamentList);
        }
    }
}