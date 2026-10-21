package com.example.sporthub.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "SportHubSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_UID = "firebaseUid";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLE = "role";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(String uid, String name, String email, String role) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_UID, uid);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_ROLE, role);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false) 
                && getFirebaseUid() != null 
                && !getFirebaseUid().trim().isEmpty();
    }

    public String getRole() {
        return pref.getString(KEY_ROLE, null);
    }

    public boolean isAdmin() {
        return isLoggedIn() && "Admin".equalsIgnoreCase(getRole());
    }

    public boolean isPlayer() {
        return isLoggedIn() && "Player".equalsIgnoreCase(getRole());
    }

    public String getName() {
        return pref.getString(KEY_NAME, "User");
    }

    public void updateSessionName(String newName) {
        editor.putString(KEY_NAME, newName);
        editor.apply();
    }

    public String getFirebaseUid() {
        return pref.getString(KEY_UID, null);
    }

    public void logoutUser() {
        editor.clear();
        editor.apply();
    }
}
