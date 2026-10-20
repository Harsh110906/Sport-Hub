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
import com.example.sporthub.adapters.MatchAdapter;
import com.example.sporthub.database.DatabaseHelper;
import com.example.sporthub.models.Match;
import com.example.sporthub.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class AdminMatchListActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private ListView lvMatches;
    private TextView tvEmptyState;
    private List<Match> matchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_match_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.toolbarAdminMatches), (v, insets) -> {
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

        Toolbar toolbar = findViewById(R.id.toolbarAdminMatches);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        dbHelper = new DatabaseHelper(this);
        lvMatches = findViewById(R.id.lvAdminMatches);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        FloatingActionButton fabAddMatch = findViewById(R.id.fabAddMatch);

        fabAddMatch.setOnClickListener(v -> {
            startActivity(new Intent(AdminMatchListActivity.this, MatchFormActivity.class));
        });

        lvMatches.setOnItemClickListener((parent, view, position, id) -> {
            Match selected = matchList.get(position);
            Intent intent = new Intent(AdminMatchListActivity.this, MatchFormActivity.class);
            intent.putExtra("MATCH_ID", selected.getId());
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMatches();
    }

    private void loadMatches() {
        matchList = dbHelper.getAllMatches();
        if (matchList.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            lvMatches.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            lvMatches.setVisibility(View.VISIBLE);
            MatchAdapter adapter = new MatchAdapter(this, matchList);
            lvMatches.setAdapter(adapter);
        }
    }
}