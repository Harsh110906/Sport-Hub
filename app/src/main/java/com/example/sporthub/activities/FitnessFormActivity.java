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
import com.example.sporthub.models.FitnessRecord;

import java.util.Calendar;
import java.util.Locale;

public class FitnessFormActivity extends AppCompatActivity {

    private Spinner spnActivityType;
    private TextView tvDate;
    private EditText etDuration, etCalories, etNotes;
    private Button btnSave, btnDelete;
    
    private DatabaseHelper dbHelper;
    private int recordId = -1;
    private int userId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitness_form);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.toolbarFitnessForm), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbarFitnessForm);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        dbHelper = new DatabaseHelper(this);

        spnActivityType = findViewById(R.id.spnActivityType);
        tvDate = findViewById(R.id.tvFitnessDate);
        etDuration = findViewById(R.id.etDuration);
        etCalories = findViewById(R.id.etCalories);
        etNotes = findViewById(R.id.etNotes);
        btnSave = findViewById(R.id.btnSaveFitness);
        btnDelete = findViewById(R.id.btnDeleteFitness);

        // Predefined list from instructions (e.g. Running, Cycling)
        String[] activities = {"Running", "Cycling", "Swimming", "Weightlifting", "Yoga", "Walking", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, activities);
        spnActivityType.setAdapter(adapter);

        tvDate.setOnClickListener(v -> showDatePicker());

        if (getIntent().hasExtra("USER_ID")) {
            userId = getIntent().getIntExtra("USER_ID", -1);
        }

        if (userId == -1) {
            Toast.makeText(this, "Security Error: Missing User ID.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (getIntent().hasExtra("RECORD_ID")) {
            recordId = getIntent().getIntExtra("RECORD_ID", -1);
        }

        if (recordId != -1) {
            getSupportActionBar().setTitle("Edit Workout");
            btnDelete.setVisibility(View.VISIBLE);
            loadRecordData();
        } else {
            getSupportActionBar().setTitle("Add Workout");
        }

        btnSave.setOnClickListener(v -> saveRecord());
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

    private void loadRecordData() {
        FitnessRecord r = dbHelper.getFitnessRecordByIdAndUser(recordId, userId);
        if (r != null) {
            tvDate.setText(r.getRecordDate());
            etDuration.setText(String.valueOf(r.getDurationMinutes()));
            if (r.getCaloriesBurned() > 0) {
                etCalories.setText(String.valueOf(r.getCaloriesBurned()));
            }
            etNotes.setText(r.getNotes());

            for (int i = 0; i < spnActivityType.getCount(); i++) {
                if (spnActivityType.getItemAtPosition(i).toString().equalsIgnoreCase(r.getActivityType())) {
                    spnActivityType.setSelection(i);
                    break;
                }
            }
        } else {
            Toast.makeText(this, "Record not found or access denied.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void saveRecord() {
        String date = tvDate.getText().toString().trim();
        String durationStr = etDuration.getText().toString().trim();
        String calStr = etCalories.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();
        String activityType = spnActivityType.getSelectedItem().toString();

        if (TextUtils.isEmpty(date) || TextUtils.isEmpty(durationStr)) {
            Toast.makeText(this, "Date and Duration are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        int duration;
        double calories = 0.0;
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

        if (!TextUtils.isEmpty(calStr)) {
            try {
                calories = Double.parseDouble(calStr);
                if (calories < 0) {
                    etCalories.setError("Cannot be negative");
                    return;
                }
            } catch (NumberFormatException e) {
                etCalories.setError("Invalid number");
                return;
            }
        }

        btnSave.setEnabled(false);
        btnSave.setText("Saving...");

        FitnessRecord r = new FitnessRecord();
        r.setUserId(userId);
        r.setActivityType(activityType);
        r.setDurationMinutes(duration);
        r.setCaloriesBurned(calories);
        r.setRecordDate(date);
        r.setNotes(notes);

        if (recordId == -1) {
            long result = dbHelper.addFitnessRecord(r);
            if (result != -1) {
                Toast.makeText(this, "Workout Saved", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error saving workout", Toast.LENGTH_SHORT).show();
                btnSave.setEnabled(true);
                btnSave.setText("Save");
            }
        } else {
            r.setId(recordId);
            int rows = dbHelper.updateFitnessRecord(r);
            if (rows > 0) {
                Toast.makeText(this, "Workout Updated", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Update failed. Access denied or record deleted.", Toast.LENGTH_LONG).show();
                btnSave.setEnabled(true);
                btnSave.setText("Save");
            }
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Workout")
                .setMessage("Are you sure you want to permanently delete this record?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    boolean success = dbHelper.deleteFitnessRecord(recordId, userId);
                    if (success) {
                        Toast.makeText(this, "Workout Deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Delete failed.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}