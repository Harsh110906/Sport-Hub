package com.example.sporthub.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
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

public class EditProfileActivity extends AppCompatActivity {

    private EditText etName, etFavSport;
    private Button btnSave;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private int localUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.toolbarEditProfile), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbarEditProfile);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Please log in to edit profile", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etName = findViewById(R.id.etEditName);
        etFavSport = findViewById(R.id.etEditFavSport);
        btnSave = findViewById(R.id.btnSaveProfile);

        loadCurrentData();

        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void loadCurrentData() {
        String uid = sessionManager.getFirebaseUid();
        if (uid == null) {
            Toast.makeText(this, "Session Error", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        User user = dbHelper.getUserByFirebaseUid(uid);
        if (user != null) {
            localUserId = user.getId();
            etName.setText(user.getName());
            
            String fav = user.getFavoriteSport();
            if (fav != null && !fav.equalsIgnoreCase("None")) {
                etFavSport.setText(fav);
            }
        } else {
            Toast.makeText(this, "Failed to load user.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void saveProfile() {
        if (localUserId == -1) {
            Toast.makeText(this, "Invalid User ID. Cannot save.", Toast.LENGTH_SHORT).show();
            return;
        }

        String newName = etName.getText().toString().trim();
        String newFavSport = etFavSport.getText().toString().trim();

        if (TextUtils.isEmpty(newName)) {
            etName.setError("Name cannot be empty");
            return;
        }

        if (TextUtils.isEmpty(newFavSport)) {
            newFavSport = "None"; // Standardize empty optional field
        }

        btnSave.setEnabled(false);
        btnSave.setText("Saving...");

        boolean success = dbHelper.updateUserProfile(localUserId, newName, newFavSport);

        if (success) {
            // Update the local session name so the Home dashboard greeting matches
            sessionManager.updateSessionName(newName);
            Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Database Error. Profile not updated.", Toast.LENGTH_SHORT).show();
            btnSave.setEnabled(true);
            btnSave.setText("Save Changes");
        }
    }
}