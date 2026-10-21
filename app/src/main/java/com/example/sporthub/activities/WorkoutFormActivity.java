package com.example.sporthub.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sporthub.R;
import com.example.sporthub.database.DatabaseHelper;
import com.example.sporthub.models.User;
import com.example.sporthub.models.WorkoutPlan;
import com.example.sporthub.utils.SessionManager;

import java.util.Calendar;
import java.util.Locale;

public class WorkoutFormActivity extends AppCompatActivity {

    private EditText etTitle, etDuration, etNotes;
    private Spinner spnActivity, spnStatus;
    private TextView tvDate;
    private Button btnSave, btnDelete;
    
    private DatabaseHelper dbHelper;
    private int planId = -1;
    private int userId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_form);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.toolbarWorkoutForm), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        SessionManager sessionManager = new SessionManager(this);
        if (!sessionManager.isPlayer()) {
            Toast.makeText(this, "Access Denied: Player access required", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbarWorkoutForm);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        dbHelper = new DatabaseHelper(this);

        User user = dbHelper.getUserByFirebaseUid(sessionManager.getFirebaseUid());
        if (user == null) {
            Toast.makeText(this, "Session Error: User profile missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        userId = user.getId();

        etTitle = findViewById(R.id.etWorkoutTitle);
        spnActivity = findViewById(R.id.spnWorkoutActivity);
        spnStatus = findViewById(R.id.spnWorkoutStatus);
        tvDate = findViewById(R.id.tvWorkoutDate);
        etDuration = findViewById(R.id.etWorkoutDuration);
        etNotes = findViewById(R.id.etWorkoutNotes);
        btnSave = findViewById(R.id.btnSaveWorkout);
        btnDelete = findViewById(R.id.btnDeleteWorkout);

        // Predefined list (Matching Fitness Tracker conventions)
        String[] activities = {"Running", "Cycling", "Swimming", "Weightlifting", "Yoga", "Walking", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, activities);
        spnActivity.setAdapter(adapter);
        
        // Status list
        String[] statuses = {"Planned", "Completed"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, statuses);
        spnStatus.setAdapter(statusAdapter);

        tvDate.setOnClickListener(v -> showDatePicker());

        if (getIntent().hasExtra("PLAN_ID")) {
            planId = getIntent().getIntExtra("PLAN_ID", -1);
        }

        if (planId != -1) {
            getSupportActionBar().setTitle("Edit Plan");
            btnDelete.setVisibility(View.VISIBLE);
            loadPlanData();
        } else {
            getSupportActionBar().setTitle("Create Plan");
        }

        btnSave.setOnClickListener(v -> savePlan());
        btnDelete.setOnClickListener(v -> confirmDelete());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String dateStr = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            tvDate.setText(dateStr);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void loadPlanData() {
        WorkoutPlan wp = dbHelper.getWorkoutPlanByIdAndUser(planId, userId);
        if (wp != null) {
            etTitle.setText(wp.getTitle());
            tvDate.setText(wp.getPlannedDate());
            etDuration.setText(String.valueOf(wp.getTargetDuration()));
            etNotes.setText(wp.getNotes());

            for (int i = 0; i < spnActivity.getCount(); i++) {
                if (spnActivity.getItemAtPosition(i).toString().equalsIgnoreCase(wp.getActivityType())) {
                    spnActivity.setSelection(i);
                    break;
                }
            }
            
            for (int i = 0; i < spnStatus.getCount(); i++) {
                if (spnStatus.getItemAtPosition(i).toString().equalsIgnoreCase(wp.getStatus())) {
                    spnStatus.setSelection(i);
                    break;
                }
            }
        } else {
            Toast.makeText(this, "Plan not found or access denied.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void savePlan() {
        String title = etTitle.getText().toString().trim();
        String date = tvDate.getText().toString().trim();
        String durationStr = etDuration.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();
        String activityType = spnActivity.getSelectedItem().toString();
        String status = spnStatus.getSelectedItem().toString();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(date) || TextUtils.isEmpty(durationStr)) {
            Toast.makeText(this, "Title, Date, and Duration are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        int duration;
        try {
            duration = Integer.parseInt(durationStr);
            if (duration <= 0) {
                etDuration.setError("Must be positive");
                return;
            }
        } catch (NumberFormatException e) {
            etDuration.setError("Invalid number");
            return;
        }

        btnSave.setEnabled(false);
        btnSave.setText("Saving...");

        WorkoutPlan wp = new WorkoutPlan();
        wp.setUserId(userId);
        wp.setTitle(title);
        wp.setActivityType(activityType);
        wp.setTargetDuration(duration);
        wp.setPlannedDate(date);
        wp.setNotes(notes);
        wp.setStatus(status);

        if (planId == -1) {
            long result = dbHelper.addWorkoutPlan(wp);
            if (result != -1) {
                Toast.makeText(this, "Plan Saved", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error saving plan", Toast.LENGTH_SHORT).show();
                btnSave.setEnabled(true);
                btnSave.setText("Save");
            }
        } else {
            wp.setId(planId);
            int rows = dbHelper.updateWorkoutPlan(wp);
            if (rows > 0) {
                Toast.makeText(this, "Plan Updated", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Update failed. Access denied or plan deleted.", Toast.LENGTH_LONG).show();
                btnSave.setEnabled(true);
                btnSave.setText("Save");
            }
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Plan")
                .setMessage("Are you sure you want to permanently delete this plan?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    boolean success = dbHelper.deleteWorkoutPlan(planId, userId);
                    if (success) {
                        Toast.makeText(this, "Plan Deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Delete failed.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}