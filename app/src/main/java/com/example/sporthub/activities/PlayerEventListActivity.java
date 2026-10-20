package com.example.sporthub.activities;

import android.content.Intent;
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
import com.example.sporthub.adapters.EventAdapter;
import com.example.sporthub.database.DatabaseHelper;
import com.example.sporthub.models.Event;

import java.util.List;

public class PlayerEventListActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private ListView lvEvents;
    private TextView tvEmptyState;
    private List<Event> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_event_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.toolbarPlayerEvents), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbarPlayerEvents);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        dbHelper = new DatabaseHelper(this);
        lvEvents = findViewById(R.id.lvPlayerEvents);
        tvEmptyState = findViewById(R.id.tvPlayerEventEmptyState);

        lvEvents.setOnItemClickListener((parent, view, position, id) -> {
            Event selected = eventList.get(position);
            Intent intent = new Intent(PlayerEventListActivity.this, EventDetailActivity.class);
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