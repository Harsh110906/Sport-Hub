package com.example.sporthub.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sporthub.R;
import com.example.sporthub.database.DatabaseHelper;
import com.example.sporthub.models.User;
import com.example.sporthub.utils.SessionManager;

public class ProfileActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private TextView tvName, tvEmail, tvRole, tvFavSport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.toolbarProfile), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbarProfile);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        tvName = findViewById(R.id.tvProfileName);
        tvEmail = findViewById(R.id.tvProfileEmail);
        tvRole = findViewById(R.id.tvProfileRole);
        tvFavSport = findViewById(R.id.tvProfileFavSport);
        Button btnEdit = findViewById(R.id.btnEditProfile);

        btnEdit.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserProfile();
    }

    private void loadUserProfile() {
        String uid = sessionManager.getFirebaseUid();
        if (uid == null) {
            Toast.makeText(this, "Session Error. Please log in again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        User user = dbHelper.getUserByFirebaseUid(uid);
        if (user != null) {
            tvName.setText(user.getName());
            tvEmail.setText(user.getEmail());
            tvRole.setText(user.getRole());
            
            String favSport = user.getFavoriteSport();
            if (favSport == null || favSport.isEmpty() || favSport.equalsIgnoreCase("None")) {
                tvFavSport.setText("Not specified");
            } else {
                tvFavSport.setText(favSport);
            }
        } else {
            Toast.makeText(this, "Failed to load profile.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}