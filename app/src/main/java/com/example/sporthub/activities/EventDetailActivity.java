package com.example.sporthub.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sporthub.R;
import com.example.sporthub.database.DatabaseHelper;
import com.example.sporthub.models.Event;

public class EventDetailActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private int eventId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        com.example.sporthub.utils.SessionManager sessionManager = new com.example.sporthub.utils.SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Please log in to view event details", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.toolbarEventDetail), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbarEventDetail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        dbHelper = new DatabaseHelper(this);

        if (getIntent().hasExtra("EVENT_ID")) {
            eventId = getIntent().getIntExtra("EVENT_ID", -1);
        }

        if (eventId != -1) {
            loadEventDetails();
        } else {
            Toast.makeText(this, "Event Not Found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadEventDetails() {
        Event e = dbHelper.getEventById(eventId);
        if (e != null) {
            TextView tvName = findViewById(R.id.tvDetEventName);
            TextView tvStatus = findViewById(R.id.tvDetEventStatus);
            TextView tvDesc = findViewById(R.id.tvDetEventDesc);
            TextView tvSport = findViewById(R.id.tvDetEventSport);
            TextView tvDate = findViewById(R.id.tvDetEventDate);
            TextView tvVenue = findViewById(R.id.tvDetEventVenue);
            TextView tvOrganizer = findViewById(R.id.tvDetEventOrganizer);

            tvName.setText(e.getName());
            tvStatus.setText(e.getStatus());
            
            String desc = (e.getDescription() == null || e.getDescription().isEmpty()) ? "No description provided." : e.getDescription();
            tvDesc.setText(desc);
            
            tvSport.setText("Sport: " + e.getSport());
            tvDate.setText("Date: " + e.getEventDate());
            
            String venue = (e.getVenue() == null || e.getVenue().isEmpty()) ? "TBD" : e.getVenue();
            tvVenue.setText("Venue: " + venue);
            
            String organizer = (e.getOrganizer() == null || e.getOrganizer().isEmpty()) ? "TBD" : e.getOrganizer();
            tvOrganizer.setText("Organizer: " + organizer);

        } else {
            Toast.makeText(this, "Event Error", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}