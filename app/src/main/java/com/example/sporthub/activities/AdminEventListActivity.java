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
import com.example.sporthub.adapters.EventAdapter;
import com.example.sporthub.database.DatabaseHelper;
import com.example.sporthub.models.Event;
import com.example.sporthub.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class AdminEventListActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private ListView lvEvents;
    private TextView tvEmptyState;
    private List<Event> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_event_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.toolbarAdminEvents), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        // Security check
        SessionManager sessionManager = new SessionManager(this);
        if (!sessionManager.isAdmin()) {
            Toast.makeText(this, "Access Denied: Admin privileges required", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbarAdminEvents);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        dbHelper = new DatabaseHelper(this);
        lvEvents = findViewById(R.id.lvAdminEvents);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        FloatingActionButton fabAddEvent = findViewById(R.id.fabAddEvent);

        fabAddEvent.setOnClickListener(v -> {
            startActivity(new Intent(AdminEventListActivity.this, EventFormActivity.class));
        });

        lvEvents.setOnItemClickListener((parent, view, position, id) -> {
            Event selected = eventList.get(position);
            Intent intent = new Intent(AdminEventListActivity.this, EventFormActivity.class);
            intent.putExtra("EVENT_ID", selected.getId());
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEvents();
    }

    private void loadEvents() {
        eventList = dbHelper.getAllEvents();
        if (eventList.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            lvEvents.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            lvEvents.setVisibility(View.VISIBLE);
            EventAdapter adapter = new EventAdapter(this, eventList);
            lvEvents.setAdapter(adapter);
        }
    }
}