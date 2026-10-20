package com.example.sporthub.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sporthub.R;
import com.example.sporthub.adapters.TournamentAdapter;
import com.example.sporthub.database.DatabaseHelper;
import com.example.sporthub.models.Tournament;

import java.util.List;

public class PlayerTournamentListActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private ListView lvTournaments;
    private TextView tvEmptyState;
    private TournamentAdapter adapter;
    private List<Tournament> tournamentList;

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

        lvTournaments.setOnItemClickListener((parent, view, position, id) -> {
            Tournament selected = tournamentList.get(position);
            Intent intent = new Intent(PlayerTournamentListActivity.this, TournamentDetailActivity.class);
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