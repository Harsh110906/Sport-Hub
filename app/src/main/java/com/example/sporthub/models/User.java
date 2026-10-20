package com.example.sporthub.models;

public class User {
    private int id;
    private String firebaseUid;
    private String name;
    private String email;
    private String role; // "Admin" or "Player"
    private String favoriteSport;

    public User() {}

    public User(String firebaseUid, String name, String email, String role, String favoriteSport) {
        this.firebaseUid = firebaseUid;
        this.name = name;
        this.email = email;
        this.role = role;
        this.favoriteSport = favoriteSport;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFirebaseUid() { return firebaseUid; }
    public void setFirebaseUid(String firebaseUid) { this.firebaseUid = firebaseUid; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getFavoriteSport() { return favoriteSport; }
    public void setFavoriteSport(String favoriteSport) { this.favoriteSport = favoriteSport; }
}