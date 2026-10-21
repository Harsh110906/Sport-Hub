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
import com.example.sporthub.models.Event;
import com.example.sporthub.utils.SessionManager;

import java.util.Calendar;
import java.util.Locale;

public class EventFormActivity extends AppCompatActivity {

    private EditText etName, etDesc, etSport, etVenue, etOrganizer;
    private TextView tvDate;
    private Spinner spnStatus;
    private Button btnSave, btnDelete;
    private DatabaseHelper dbHelper;
    private int eventId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_form);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.toolbarEventForm), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        SessionManager sessionManager = new SessionManager(this);
        if (!sessionManager.isAdmin()) {
            Toast.makeText(this, "Access Denied: Admin privileges required", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbarEventForm);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        dbHelper = new DatabaseHelper(this);

        etName = findViewById(R.id.etEventName);
        etDesc = findViewById(R.id.etEventDesc);
        etSport = findViewById(R.id.etEventSport);
        tvDate = findViewById(R.id.tvEventDate);
        etVenue = findViewById(R.id.etEventVenue);
        etOrganizer = findViewById(R.id.etEventOrganizer);
        spnStatus = findViewById(R.id.spnEventStatus);
        btnSave = findViewById(R.id.btnSaveEvent);
        btnDelete = findViewById(R.id.btnDeleteEvent);

        String[] statuses = {"Upcoming", "Ongoing", "Completed", "Cancelled"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, statuses);
        spnStatus.setAdapter(statusAdapter);

        tvDate.setOnClickListener(v -> showDatePicker());

        if (getIntent().hasExtra("EVENT_ID")) {
            eventId = getIntent().getIntExtra("EVENT_ID", -1);
        }

        if (eventId != -1) {
            getSupportActionBar().setTitle("Edit Event");
            btnDelete.setVisibility(View.VISIBLE);
            loadEventData();
        } else {
            getSupportActionBar().setTitle("Create Event");
        }

        btnSave.setOnClickListener(v -> saveEvent());
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

    private void loadEventData() {
        Event e = dbHelper.getEventById(eventId);
        if (e != null) {
            etName.setText(e.getName());
            etDesc.setText(e.getDescription());
            etSport.setText(e.getSport());
            tvDate.setText(e.getEventDate());
            etVenue.setText(e.getVenue());
            etOrganizer.setText(e.getOrganizer());

            for (int i = 0; i < spnStatus.getCount(); i++) {
                if (spnStatus.getItemAtPosition(i).toString().equalsIgnoreCase(e.getStatus())) {
                    spnStatus.setSelection(i);
                    break;
                }
            }
        } else {
            Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void saveEvent() {
        String name = etName.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        String sport = etSport.getText().toString().trim();
        String date = tvDate.getText().toString().trim();
        String venue = etVenue.getText().toString().trim();
        String organizer = etOrganizer.getText().toString().trim();
        String status = spnStatus.getSelectedItem().toString();

        if (TextUtils.isEmpty(name)) {
            etName.setError("Event Name is required");
            etName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(sport)) {
            etSport.setError("Sport is required");
            etSport.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(date)) {
            Toast.makeText(this, "Event Date is required", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSave.setEnabled(false);
        btnSave.setText("Saving...");

        Event e = new Event();
        e.setName(name);
        e.setDescription(desc);
        e.setSport(sport);
        e.setEventDate(date);
        e.setVenue(venue);
        e.setOrganizer(organizer);
        e.setStatus(status);

        if (eventId == -1) {
            long result = dbHelper.addEvent(e);
            if (result != -1) {
                Toast.makeText(this, "Event Created", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error creating event", Toast.LENGTH_SHORT).show();
                btnSave.setEnabled(true);
                btnSave.setText("Save");
            }
        } else {
            e.setId(eventId);
            int rows = dbHelper.updateEvent(e);
            if (rows > 0) {
                Toast.makeText(this, "Event Updated", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error updating event", Toast.LENGTH_SHORT).show();
                btnSave.setEnabled(true);
                btnSave.setText("Save");
            }
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to permanently delete this event?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    boolean success = dbHelper.deleteEvent(eventId);
                    if (success) {
                        Toast.makeText(this, "Event Deleted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error deleting event", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }
}