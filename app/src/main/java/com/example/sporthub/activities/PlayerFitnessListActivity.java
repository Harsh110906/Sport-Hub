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
import com.example.sporthub.adapters.FitnessAdapter;
import com.example.sporthub.database.DatabaseHelper;
import com.example.sporthub.models.FitnessRecord;
import com.example.sporthub.models.User;
import com.example.sporthub.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class PlayerFitnessListActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private ListView lvFitness;
    private TextView tvEmptyState, tvTotalWorkouts, tvTotalMinutes, tvTotalCalories;
    private List<FitnessRecord> recordList;
    private int localUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_fitness_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.toolbarPlayerFitness), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        sessionManager = new SessionManager(this);

        // Security check - Player only
        if (!sessionManager.isPlayer()) {
            Toast.makeText(this, "Access Denied: Player access required", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbarPlayerFitness);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        dbHelper = new DatabaseHelper(this);

        // Resolve Local User ID securely
        User user = dbHelper.getUserByFirebaseUid(sessionManager.getFirebaseUid());
        if (user == null) {
            Toast.makeText(this, "Local user profile missing. Please log in again.", Toast.LENGTH_LONG).show();
            sessionManager.logoutUser();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        localUserId = user.getId();

        tvTotalWorkouts = findViewById(R.id.tvTotalWorkouts);
        tvTotalMinutes = findViewById(R.id.tvTotalMinutes);
        tvTotalCalories = findViewById(R.id.tvTotalCalories);
        tvEmptyState = findViewById(R.id.tvFitnessEmptyState);
        lvFitness = findViewById(R.id.lvPlayerFitness);
        FloatingActionButton fabAddFitness = findViewById(R.id.fabAddFitness);

        fabAddFitness.setOnClickListener(v -> {
            Intent intent = new Intent(PlayerFitnessListActivity.this, FitnessFormActivity.class);
            intent.putExtra("USER_ID", localUserId);
            startActivity(intent);
        });

        lvFitness.setOnItemClickListener((parent, view, position, id) -> {
            FitnessRecord selected = recordList.get(position);
            Intent intent = new Intent(PlayerFitnessListActivity.this, FitnessFormActivity.class);
            intent.putExtra("RECORD_ID", selected.getId());
            intent.putExtra("USER_ID", localUserId);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (localUserId != -1) {
            loadFitnessData();
        }
    }

    private void loadFitnessData() {
        recordList = dbHelper.getFitnessRecordsByUserId(localUserId);
        
        int totalMins = 0;
        double totalCals = 0.0;
        for (FitnessRecord r : recordList) {
            totalMins += r.getDurationMinutes();
            totalCals += r.getCaloriesBurned();
        }

        tvTotalWorkouts.setText(String.valueOf(recordList.size()));
        tvTotalMinutes.setText(String.valueOf(totalMins));
        
        // Format to remove decimal if it's a whole number, otherwise show up to 1 decimal place
        if (totalCals == (long) totalCals) {
            tvTotalCalories.setText(String.format(java.util.Locale.US, "%d", (long) totalCals));
        } else {
            tvTotalCalories.setText(String.format(java.util.Locale.US, "%.1f", totalCals));
        }

        if (recordList.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            lvFitness.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            lvFitness.setVisibility(View.VISIBLE);
            FitnessAdapter adapter = new FitnessAdapter(this, recordList);
            lvFitness.setAdapter(adapter);
        }
    }
}