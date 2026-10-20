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
import com.example.sporthub.models.Tournament;
import com.example.sporthub.utils.SessionManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TournamentFormActivity extends AppCompatActivity {

    private EditText etName, etSport, etVenue, etOrganizer, etMaxTeams;
    private TextView tvStartDate, tvEndDate;
    private Spinner spnStatus;
    private Button btnSave, btnDelete;
    private DatabaseHelper dbHelper;
    private int tournamentId = -1; // -1 means Create Mode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament_form);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.toolbarForm), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        SessionManager sessionManager = new SessionManager(this);
        if (!"Admin".equalsIgnoreCase(sessionManager.getRole())) {
            Toast.makeText(this, "Access Denied", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbarForm);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        dbHelper = new DatabaseHelper(this);

        etName = findViewById(R.id.etName);
        etSport = findViewById(R.id.etSport);
        etVenue = findViewById(R.id.etVenue);
        etOrganizer = findViewById(R.id.etOrganizer);
        etMaxTeams = findViewById(R.id.etMaxTeams);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        spnStatus = findViewById(R.id.spnStatus);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        // Setup Spinner
        String[] statuses = {"Upcoming", "Ongoing", "Completed"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, statuses);
        spnStatus.setAdapter(spinnerAdapter);

        // Setup Date Pickers
        tvStartDate.setOnClickListener(v -> showDatePicker(tvStartDate));
        tvEndDate.setOnClickListener(v -> showDatePicker(tvEndDate));

        if (getIntent().hasExtra("TOURNAMENT_ID")) {
            tournamentId = getIntent().getIntExtra("TOURNAMENT_ID", -1);
        }

        if (tournamentId != -1) {
            getSupportActionBar().setTitle("Edit Tournament");
            btnDelete.setVisibility(View.VISIBLE);
            loadTournamentData();
        } else {
            getSupportActionBar().setTitle("Create Tournament");
        }

        btnSave.setOnClickListener(v -> saveTournament());
        btnDelete.setOnClickListener(v -> confirmDelete());
    }

    private void showDatePicker(TextView target) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String dateStr = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            target.setText(dateStr);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void loadTournamentData() {
        Tournament t = dbHelper.getTournamentById(tournamentId);
        if (t != null) {
            etName.setText(t.getName());
            etSport.setText(t.getSport());
            etVenue.setText(t.getVenue());
            etOrganizer.setText(t.getOrganizer());
            etMaxTeams.setText(String.valueOf(t.getMaxTeams()));
            tvStartDate.setText(t.getStartDate());
            tvEndDate.setText(t.getEndDate());

            // Set Spinner Selection
            for (int i = 0; i < spnStatus.getCount(); i++) {
                if (spnStatus.getItemAtPosition(i).toString().equalsIgnoreCase(t.getStatus())) {
                    spnStatus.setSelection(i);
                    break;
                }
            }
        }
    }

    private void saveTournament() {
        String name = etName.getText().toString().trim();
        String sport = etSport.getText().toString().trim();
        String venue = etVenue.getText().toString().trim();
        String organizer = etOrganizer.getText().toString().trim();
        String maxTeamsStr = etMaxTeams.getText().toString().trim();
        String startDate = tvStartDate.getText().toString().trim();
        String endDate = tvEndDate.getText().toString().trim();
        String status = spnStatus.getSelectedItem().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(sport) || TextUtils.isEmpty(startDate) || TextUtils.isEmpty(endDate) || TextUtils.isEmpty(maxTeamsStr)) {
            Toast.makeText(this, "Please fill all required fields (*)", Toast.LENGTH_SHORT).show();
            return;
        }

        int maxTeams;
        try {
            maxTeams = Integer.parseInt(maxTeamsStr);
            if (maxTeams <= 0) {
                etMaxTeams.setError("Must be positive integer");
                return;
            }
        } catch (NumberFormatException e) {
            etMaxTeams.setError("Invalid number");
            return;
        }

        // Date Validation (yyyy-MM-dd format)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        sdf.setLenient(false);
        try {
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);
            if (start != null && end != null && end.before(start)) {
                Toast.makeText(this, "End date cannot be before start date", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (ParseException e) {
            Toast.makeText(this, "Invalid date format. Please use YYYY-MM-DD", Toast.LENGTH_SHORT).show();
            return;
        }

        Tournament t = new Tournament();
        t.setName(name);
        t.setSport(sport);
        t.setVenue(venue);
        t.setOrganizer(organizer);
        t.setMaxTeams(maxTeams);
        t.setStartDate(startDate);
        t.setEndDate(endDate);
        t.setStatus(status);

        if (tournamentId == -1) {
            long result = dbHelper.addTournament(t);
            if (result != -1) {
                Toast.makeText(this, "Tournament Created", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error creating tournament", Toast.LENGTH_SHORT).show();
            }
        } else {
            t.setId(tournamentId);
            int rows = dbHelper.updateTournament(t);
            if (rows > 0) {
                Toast.makeText(this, "Tournament Updated", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error updating tournament", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Tournament")
                .setMessage("Are you sure you want to permanently delete this tournament?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dbHelper.deleteTournament(tournamentId);
                    Toast.makeText(this, "Tournament Deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }
}