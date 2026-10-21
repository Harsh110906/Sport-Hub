package com.example.sporthub.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sporthub.R;
import com.example.sporthub.database.DatabaseHelper;
import com.example.sporthub.models.User;
import com.example.sporthub.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SessionManager sessionManager = new SessionManager(SplashActivity.this);
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            
            boolean isValidSession = sessionManager.isLoggedIn() 
                    && currentUser != null 
                    && currentUser.getUid().equals(sessionManager.getFirebaseUid());

            Intent intent;
            if (isValidSession) {
                DatabaseHelper dbHelper = new DatabaseHelper(SplashActivity.this);
                User user = dbHelper.getUserByFirebaseUid(currentUser.getUid());
                if (user != null) {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                } else {
                    FirebaseAuth.getInstance().signOut();
                    sessionManager.logoutUser();
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                }
            } else {
                FirebaseAuth.getInstance().signOut();
                sessionManager.logoutUser();
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }, SPLASH_DURATION);
    }
}
