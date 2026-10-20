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
import com.example.sporthub.adapters.TeamAdapter;
import com.example.sporthub.database.DatabaseHelper;
import com.example.sporthub.models.Team;
import com.example.sporthub.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class AdminTeamListActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private ListView lvTeams;
    private TextView tvEmptyState;
    private List<Team> teamList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_team_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.toolbarAdminTeams), (v, insets) -> {
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

        Toolbar toolbar = findViewById(R.id.toolbarAdminTeams);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        dbHelper = new DatabaseHelper(this);
        lvTeams = findViewById(R.id.lvAdminTeams);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        FloatingActionButton fabAddTeam = findViewById(R.id.fabAddTeam);

        fabAddTeam.setOnClickListener(v -> {
            Intent intent = new Intent(AdminTeamListActivity.this, TeamFormActivity.class);
            startActivity(intent);
        });

        lvTeams.setOnItemClickListener((parent, view, position, id) -> {
            Team selected = teamList.get(position);
            Intent intent = new Intent(AdminTeamListActivity.this, TeamFormActivity.class);
            intent.putExtra("TEAM_ID", selected.getId());
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTeams();
    }

    private void loadTeams() {
        teamList = dbHelper.getAllTeams();
        if (teamList.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            lvTeams.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            lvTeams.setVisibility(View.VISIBLE);
            TeamAdapter adapter = new TeamAdapter(this, teamList);
            lvTeams.setAdapter(adapter);
        }
    }
}