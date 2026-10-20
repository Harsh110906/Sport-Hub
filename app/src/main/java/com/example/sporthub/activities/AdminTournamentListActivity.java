package com.example.sporthub.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sporthub.R;
import com.example.sporthub.adapters.TournamentAdapter;
import com.example.sporthub.database.DatabaseHelper;
import com.example.sporthub.models.Tournament;
import com.example.sporthub.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class AdminTournamentListActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private ListView lvTournaments;
    private TextView tvEmptyState;
    private TournamentAdapter adapter;
    private List<Tournament> tournamentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_tournament_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.toolbarAdminList), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        // Security Role Check
        SessionManager sessionManager = new SessionManager(this);
        if (!"Admin".equalsIgnoreCase(sessionManager.getRole())) {
            Toast.makeText(this, "Access Denied: Admin Only", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbarAdminList);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        dbHelper = new DatabaseHelper(this);
        lvTournaments = findViewById(R.id.lvAdminTournaments);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        FloatingActionButton fabAddTournament = findViewById(R.id.fabAddTournament);

        fabAddTournament.setOnClickListener(v -> {
            Intent intent = new Intent(AdminTournamentListActivity.this, TournamentFormActivity.class);
            startActivity(intent);
        });

        lvTournaments.setOnItemClickListener((parent, view, position, id) -> {
            Tournament selected = tournamentList.get(position);
            Intent intent = new Intent(AdminTournamentListActivity.this, TournamentFormActivity.class);
            intent.putExtra("TOURNAMENT_ID", selected.getId());
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTournaments();
    }

    private void loadTournaments() {
        tournamentList = dbHelper.getAllTournaments();
        if (tournamentList.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            lvTournaments.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            lvTournaments.setVisibility(View.VISIBLE);
            adapter = new TournamentAdapter(this, tournamentList);
            lvTournaments.setAdapter(adapter);
        }
    }
}