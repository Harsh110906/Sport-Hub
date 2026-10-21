package com.example.sporthub.activities;

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
import com.example.sporthub.adapters.MatchAdapter;
import com.example.sporthub.database.DatabaseHelper;
import com.example.sporthub.models.Match;
import com.example.sporthub.utils.SessionManager;

import java.util.List;

public class PlayerMatchListActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private ListView lvMatches;
    private TextView tvEmptyState;
    private List<Match> matchList;

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