package com.example.sporthub.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SportHubValidator {

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        String trimmed = email.trim();
        if (trimmed.isEmpty()) return false;
        // Pure Java email validation regex (no Android framework dependency)
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return trimmed.matches(emailRegex);
    }

    public static boolean isValidDate(String dateStr) {
        if (dateStr == null) return false;
        String trimmed = dateStr.trim();
        if (!trimmed.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            return false;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            sdf.setLenient(false); // Strict calendar date parsing (rejects 2023-02-30, etc.)
            Date date = sdf.parse(trimmed);
            return date != null;
        } catch (ParseException e) {
            return false;
        }
    }

    public static boolean isStartDateBeforeOrEqualEnd(String startDateStr, String endDateStr) {
        if (!isValidDate(startDateStr) || !isValidDate(endDateStr)) {
            return false;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            sdf.setLenient(false);
            Date start = sdf.parse(startDateStr);
            Date end = sdf.parse(endDateStr);
            return start != null && end != null && !start.after(end);
        } catch (ParseException e) {
            return false;
        }
    }

    public static boolean isValidDuration(int durationMinutes) {
        return durationMinutes > 0;
    }

    public static boolean isValidDuration(String durationStr) {
        if (durationStr == null) return false;
        try {
            int val = Integer.parseInt(durationStr.trim());
            return val > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidScore(Integer score) {
        return score == null || score >= 0;
    }

    public static boolean isValidScore(String scoreStr) {
        if (scoreStr == null || scoreStr.trim().isEmpty()) return true;
        try {
            int val = Integer.parseInt(scoreStr.trim());
            return val >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean areTeamsValidForMatch(int teamAId, int teamBId) {
        return teamAId != -1 && teamBId != -1 && teamAId != teamBId;
    }

    public static boolean canRegisterForTournament(String tournamentStatus, boolean isAlreadyRegistered) {
        return "Upcoming".equalsIgnoreCase(tournamentStatus) && !isAlreadyRegistered;
    }

    public static boolean canCancelRegistration(String tournamentStatus, boolean isRegistered) {
        return "Upcoming".equalsIgnoreCase(tournamentStatus) && isRegistered;
    }
}
