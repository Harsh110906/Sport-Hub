package com.example.sporthub.models;

public class WorkoutPlan {
    private int id;
    private int userId;
    private String title;
    private String activityType;
    private String plannedDate;
    private int targetDuration;
    private String notes;

    public WorkoutPlan() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }

    public String getPlannedDate() { return plannedDate; }
    public void setPlannedDate(String plannedDate) { this.plannedDate = plannedDate; }

    public int getTargetDuration() { return targetDuration; }
    public void setTargetDuration(int targetDuration) { this.targetDuration = targetDuration; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}