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
import com.example.sporthub.adapters.WorkoutAdapter;
import com.example.sporthub.database.DatabaseHelper;
import com.example.sporthub.models.User;
import com.example.sporthub.models.WorkoutPlan;
import com.example.sporthub.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class PlayerWorkoutListActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private ListView lvWorkouts;
    private TextView tvEmptyState;
    private List<WorkoutPlan> workoutList;
    private int localUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_workout_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.toolbarWorkoutList), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        SessionManager sessionManager = new SessionManager(this);

        // Security check - Player only
        if (!sessionManager.isLoggedIn() || !"Player".equalsIgnoreCase(sessionManager.getRole()) || sessionManager.getFirebaseUid() == null) {
            Toast.makeText(this, "Access Denied", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbarWorkoutList);
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

        tvEmptyState = findViewById(R.id.tvWorkoutEmptyState);
        lvWorkouts = findViewById(R.id.lvWorkouts);
        FloatingActionButton fabAddWorkout = findViewById(R.id.fabAddWorkout);

        fabAddWorkout.setOnClickListener(v -> {
            Intent intent = new Intent(PlayerWorkoutListActivity.this, WorkoutFormActivity.class);
            intent.putExtra("USER_ID", localUserId);
            startActivity(intent);
        });

        lvWorkouts.setOnItemClickListener((parent, view, position, id) -> {
            WorkoutPlan selected = workoutList.get(position);
            Intent intent = new Intent(PlayerWorkoutListActivity.this, WorkoutFormActivity.class);
            intent.putExtra("PLAN_ID", selected.getId());
            intent.putExtra("USER_ID", localUserId);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (localUserId != -1) {
            loadWorkoutData();
        }
    }

    private void loadWorkoutData() {
        workoutList = dbHelper.getWorkoutPlansByUserId(localUserId);
        
        if (workoutList.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            lvWorkouts.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            lvWorkouts.setVisibility(View.VISIBLE);
            WorkoutAdapter adapter = new WorkoutAdapter(this, workoutList);
            lvWorkouts.setAdapter(adapter);
        }
    }
}