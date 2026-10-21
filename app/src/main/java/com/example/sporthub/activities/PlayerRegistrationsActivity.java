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

import java.util.List;

public class PlayerRegistrationsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private ListView lvRegistrations;
    private TextView tvEmptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_registrations);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.toolbarMyRegistrations), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        sessionManager = new SessionManager(this);

        // Security check
        if (!sessionManager.isPlayer()) {
            Toast.makeText(this, "Access Denied: Player access required", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbarMyRegistrations);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        dbHelper = new DatabaseHelper(this);
        lvRegistrations = findViewById(R.id.lvMyRegistrations);
        tvEmptyState = findViewById(R.id.tvRegEmptyState);

        // Allow clicking the item to view details
        lvRegistrations.setOnItemClickListener((parent, view, position, id) -> {
            Tournament selected = (Tournament) parent.getItemAtPosition(position);
            Intent intent = new Intent(PlayerRegistrationsActivity.this, TournamentDetailActivity.class);
            intent.putExtra("TOURNAMENT_ID", selected.getId());
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRegistrations();
    }

    private void loadRegistrations() {
        String uid = sessionManager.getFirebaseUid();
        if (uid != null) {
            List<Tournament> regList = dbHelper.getPlayerRegistrations(uid);
            if (regList.isEmpty()) {
                tvEmptyState.setVisibility(View.VISIBLE);
                lvRegistrations.setVisibility(View.GONE);
            } else {
                tvEmptyState.setVisibility(View.GONE);
                lvRegistrations.setVisibility(View.VISIBLE);
                TournamentAdapter adapter = new TournamentAdapter(this, regList);
                lvRegistrations.setAdapter(adapter);
            }
        }
    }
}