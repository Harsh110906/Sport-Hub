package com.example.sporthub.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.sporthub.models.Event;
import com.example.sporthub.models.Tournament;
import com.example.sporthub.models.User;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SportHub.db";
    private static final int DATABASE_VERSION = 4;

    // Users Table (Existing)
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_UID = "firebaseUid";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_ROLE = "role";
    private static final String COLUMN_FAV_SPORT = "favoriteSport";

    // Tournaments Table
    private static final String TABLE_TOURNAMENTS = "tournaments";
    private static final String COL_TOURNAMENT_ID = "id";
    private static final String COL_TOURNAMENT_NAME = "name";
    private static final String COL_TOURNAMENT_SPORT = "sport";
    private static final String COL_TOURNAMENT_ORGANIZER = "organizer";
    private static final String COL_TOURNAMENT_START = "start_date";
    private static final String COL_TOURNAMENT_END = "end_date";
    private static final String COL_TOURNAMENT_VENUE = "venue";
    private static final String COL_TOURNAMENT_MAX_TEAMS = "max_teams";
    private static final String COL_TOURNAMENT_STATUS = "status";

    // Teams Table
    private static final String TABLE_TEAMS = "teams";
    private static final String COL_TEAM_ID = "id";
    private static final String COL_TEAM_NAME = "name";
    private static final String COL_TEAM_SPORT = "sport";
    private static final String COL_TEAM_CAPTAIN = "captain";
    private static final String COL_TEAM_CONTACT = "contact_email";
    private static final String COL_TEAM_TID = "tournament_id"; // Foreign key

    // Matches Table
    private static final String TABLE_MATCHES = "matches";
    private static final String COL_MATCH_ID = "id";
    private static final String COL_MATCH_TID = "tournament_id";
    private static final String COL_MATCH_TEAM_A = "team_a_id";
    private static final String COL_MATCH_TEAM_B = "team_b_id";
    private static final String COL_MATCH_DATE = "match_date";
    private static final String COL_MATCH_VENUE = "venue";
    private static final String COL_MATCH_STATUS = "status";
    private static final String COL_MATCH_SCORE_A = "score_a";
    private static final String COL_MATCH_SCORE_B = "score_b";

    // Fitness Table
    private static final String TABLE_FITNESS = "fitness_records";
    private static final String COL_FITNESS_ID = "id";
    private static final String COL_FITNESS_UID = "user_id"; // Foreign key
    private static final String COL_FITNESS_ACTIVITY = "activity_type";
    private static final String COL_FITNESS_DURATION = "duration_minutes";
    private static final String COL_FITNESS_CALORIES = "calories_burned";
    private static final String COL_FITNESS_DATE = "record_date";
    private static final String COL_FITNESS_NOTES = "notes";

    // Events Table
    private static final String TABLE_EVENTS = "events";
    private static final String COL_EVENT_ID = "id";
    private static final String COL_EVENT_NAME = "name";
    private static final String COL_EVENT_DESC = "description";
    private static final String COL_EVENT_SPORT = "sport";
    private static final String COL_EVENT_DATE = "event_date";
    private static final String COL_EVENT_VENUE = "venue";
    private static final String COL_EVENT_ORGANIZER = "organizer";
    private static final String COL_EVENT_STATUS = "status";

    // Tournament Registrations Table (Phase 6)
    private static final String TABLE_REGISTRATIONS = "tournament_registrations";
    private static final String COL_REG_ID = "id";
    private static final String COL_REG_UID = "user_id";
    private static final String COL_REG_TID = "tournament_id";
    private static final String COL_REG_DATE = "registration_date";

    // Workout Plans Table (Phase 10)
    private static final String TABLE_WORKOUTS = "workout_plans";
    private static final String COL_WORKOUT_ID = "id";
    private static final String COL_WORKOUT_UID = "user_id"; // Foreign key
    private static final String COL_WORKOUT_TITLE = "title";
    private static final String COL_WORKOUT_ACTIVITY = "activity_type";
    private static final String COL_WORKOUT_DATE = "planned_date";
    private static final String COL_WORKOUT_DURATION = "target_duration";
    private static final String COL_WORKOUT_NOTES = "notes";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users Table
        String CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_UID + " TEXT UNIQUE,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_EMAIL + " TEXT,"
                + COLUMN_ROLE + " TEXT,"
                + COLUMN_FAV_SPORT + " TEXT" + ")";
        db.execSQL(CREATE_USERS_TABLE);

        createPhase4Tables(db);
        createPhase6Tables(db);
        createPhase10Tables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            createPhase4Tables(db);
        }
        if (oldVersion < 3) {
            createPhase6Tables(db);
        }
        if (oldVersion < 4) {
            createPhase10Tables(db);
        }
    }

    private void createPhase10Tables(SQLiteDatabase db) {
        String CREATE_WORKOUTS = "CREATE TABLE IF NOT EXISTS " + TABLE_WORKOUTS + " ("
                + COL_WORKOUT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_WORKOUT_UID + " INTEGER NOT NULL,"
                + COL_WORKOUT_TITLE + " TEXT NOT NULL,"
                + COL_WORKOUT_ACTIVITY + " TEXT NOT NULL,"
                + COL_WORKOUT_DATE + " TEXT NOT NULL,"
                + COL_WORKOUT_DURATION + " INTEGER,"
                + COL_WORKOUT_NOTES + " TEXT,"
                + "FOREIGN KEY(" + COL_WORKOUT_UID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "))";
        db.execSQL(CREATE_WORKOUTS);
    }

    private void createPhase6Tables(SQLiteDatabase db) {
        String CREATE_REGISTRATIONS = "CREATE TABLE IF NOT EXISTS " + TABLE_REGISTRATIONS + " ("
                + COL_REG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_REG_UID + " INTEGER NOT NULL,"
                + COL_REG_TID + " INTEGER NOT NULL,"
                + COL_REG_DATE + " TEXT NOT NULL,"
                + "FOREIGN KEY(" + COL_REG_UID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "),"
                + "FOREIGN KEY(" + COL_REG_TID + ") REFERENCES " + TABLE_TOURNAMENTS + "(" + COL_TOURNAMENT_ID + "),"
                + "UNIQUE(" + COL_REG_UID + ", " + COL_REG_TID + "))";
        db.execSQL(CREATE_REGISTRATIONS);
    }

    private void createPhase4Tables(SQLiteDatabase db) {
        String CREATE_TOURNAMENTS = "CREATE TABLE IF NOT EXISTS " + TABLE_TOURNAMENTS + " ("
                + COL_TOURNAMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_TOURNAMENT_NAME + " TEXT NOT NULL,"
                + COL_TOURNAMENT_SPORT + " TEXT NOT NULL,"
                + COL_TOURNAMENT_ORGANIZER + " TEXT,"
                + COL_TOURNAMENT_START + " TEXT,"
                + COL_TOURNAMENT_END + " TEXT,"
                + COL_TOURNAMENT_VENUE + " TEXT,"
                + COL_TOURNAMENT_MAX_TEAMS + " INTEGER,"
                + COL_TOURNAMENT_STATUS + " TEXT)";
        db.execSQL(CREATE_TOURNAMENTS);

        String CREATE_TEAMS = "CREATE TABLE IF NOT EXISTS " + TABLE_TEAMS + " ("
                + COL_TEAM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_TEAM_NAME + " TEXT NOT NULL,"
                + COL_TEAM_SPORT + " TEXT NOT NULL,"
                + COL_TEAM_CAPTAIN + " TEXT,"
                + COL_TEAM_CONTACT + " TEXT,"
                + COL_TEAM_TID + " INTEGER,"
                + "FOREIGN KEY(" + COL_TEAM_TID + ") REFERENCES " + TABLE_TOURNAMENTS + "(" + COL_TOURNAMENT_ID + "))";
        db.execSQL(CREATE_TEAMS);

        String CREATE_MATCHES = "CREATE TABLE IF NOT EXISTS " + TABLE_MATCHES + " ("
                + COL_MATCH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_MATCH_TID + " INTEGER,"
                + COL_MATCH_TEAM_A + " INTEGER,"
                + COL_MATCH_TEAM_B + " INTEGER,"
                + COL_MATCH_DATE + " TEXT,"
                + COL_MATCH_VENUE + " TEXT,"
                + COL_MATCH_STATUS + " TEXT,"
                + COL_MATCH_SCORE_A + " INTEGER,"
                + COL_MATCH_SCORE_B + " INTEGER,"
                + "FOREIGN KEY(" + COL_MATCH_TID + ") REFERENCES " + TABLE_TOURNAMENTS + "(" + COL_TOURNAMENT_ID + "),"
                + "FOREIGN KEY(" + COL_MATCH_TEAM_A + ") REFERENCES " + TABLE_TEAMS + "(" + COL_TEAM_ID + "),"
                + "FOREIGN KEY(" + COL_MATCH_TEAM_B + ") REFERENCES " + TABLE_TEAMS + "(" + COL_TEAM_ID + "))";
        db.execSQL(CREATE_MATCHES);

        String CREATE_FITNESS = "CREATE TABLE IF NOT EXISTS " + TABLE_FITNESS + " ("
                + COL_FITNESS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_FITNESS_UID + " INTEGER,"
                + COL_FITNESS_ACTIVITY + " TEXT NOT NULL,"
                + COL_FITNESS_DURATION + " INTEGER,"
                + COL_FITNESS_CALORIES + " REAL,"
                + COL_FITNESS_DATE + " TEXT,"
                + COL_FITNESS_NOTES + " TEXT,"
                + "FOREIGN KEY(" + COL_FITNESS_UID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "))";
        db.execSQL(CREATE_FITNESS);

        String CREATE_EVENTS = "CREATE TABLE IF NOT EXISTS " + TABLE_EVENTS + " ("
                + COL_EVENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_EVENT_NAME + " TEXT NOT NULL,"
                + COL_EVENT_DESC + " TEXT,"
                + COL_EVENT_SPORT + " TEXT,"
                + COL_EVENT_DATE + " TEXT,"
                + COL_EVENT_VENUE + " TEXT,"
                + COL_EVENT_ORGANIZER + " TEXT,"
                + COL_EVENT_STATUS + " TEXT)";
        db.execSQL(CREATE_EVENTS);
    }

    // ==========================================
    // USERS CRUD (Existing Phase 2 functionality)
    // ==========================================

    public long addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_UID, user.getFirebaseUid());
        values.put(COLUMN_NAME, user.getName());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_ROLE, user.getRole());
        values.put(COLUMN_FAV_SPORT, user.getFavoriteSport());
        
        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id;
    }

    public User getUserByFirebaseUid(String uid) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, 
                new String[]{COLUMN_ID, COLUMN_UID, COLUMN_NAME, COLUMN_EMAIL, COLUMN_ROLE, COLUMN_FAV_SPORT},
                COLUMN_UID + "=?", new String[]{uid}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            user.setFirebaseUid(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UID)));
            user.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
            user.setRole(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE)));
            user.setFavoriteSport(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FAV_SPORT)));
            cursor.close();
            return user;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    // ==========================================
    // TOURNAMENTS CRUD (Phase 4)
    // ==========================================

    public long addTournament(Tournament t) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TOURNAMENT_NAME, t.getName());
        values.put(COL_TOURNAMENT_SPORT, t.getSport());
        values.put(COL_TOURNAMENT_ORGANIZER, t.getOrganizer());
        values.put(COL_TOURNAMENT_START, t.getStartDate());
        values.put(COL_TOURNAMENT_END, t.getEndDate());
        values.put(COL_TOURNAMENT_VENUE, t.getVenue());
        values.put(COL_TOURNAMENT_MAX_TEAMS, t.getMaxTeams());
        values.put(COL_TOURNAMENT_STATUS, t.getStatus());
        long id = db.insert(TABLE_TOURNAMENTS, null, values);
        db.close();
        return id;
    }

    public List<Tournament> getAllTournaments() {
        List<Tournament> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TOURNAMENTS, null);
        if (cursor.moveToFirst()) {
            do {
                Tournament t = new Tournament();
                t.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_TOURNAMENT_ID)));
                t.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_TOURNAMENT_NAME)));
                t.setSport(cursor.getString(cursor.getColumnIndexOrThrow(COL_TOURNAMENT_SPORT)));
                t.setOrganizer(cursor.getString(cursor.getColumnIndexOrThrow(COL_TOURNAMENT_ORGANIZER)));
                t.setStartDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_TOURNAMENT_START)));
                t.setEndDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_TOURNAMENT_END)));
                t.setVenue(cursor.getString(cursor.getColumnIndexOrThrow(COL_TOURNAMENT_VENUE)));
                t.setMaxTeams(cursor.getInt(cursor.getColumnIndexOrThrow(COL_TOURNAMENT_MAX_TEAMS)));
                t.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COL_TOURNAMENT_STATUS)));
                list.add(t);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public Tournament getTournamentById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TOURNAMENTS, null, COL_TOURNAMENT_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Tournament t = new Tournament();
            t.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_TOURNAMENT_ID)));
            t.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_TOURNAMENT_NAME)));
            t.setSport(cursor.getString(cursor.getColumnIndexOrThrow(COL_TOURNAMENT_SPORT)));
            t.setOrganizer(cursor.getString(cursor.getColumnIndexOrThrow(COL_TOURNAMENT_ORGANIZER)));
            t.setStartDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_TOURNAMENT_START)));
            t.setEndDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_TOURNAMENT_END)));
            t.setVenue(cursor.getString(cursor.getColumnIndexOrThrow(COL_TOURNAMENT_VENUE)));
            t.setMaxTeams(cursor.getInt(cursor.getColumnIndexOrThrow(COL_TOURNAMENT_MAX_TEAMS)));
            t.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COL_TOURNAMENT_STATUS)));
            cursor.close();
            return t;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    public int updateTournament(Tournament t) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TOURNAMENT_NAME, t.getName());
        values.put(COL_TOURNAMENT_SPORT, t.getSport());
        values.put(COL_TOURNAMENT_ORGANIZER, t.getOrganizer());
        values.put(COL_TOURNAMENT_START, t.getStartDate());
        values.put(COL_TOURNAMENT_END, t.getEndDate());
        values.put(COL_TOURNAMENT_VENUE, t.getVenue());
        values.put(COL_TOURNAMENT_MAX_TEAMS, t.getMaxTeams());
        values.put(COL_TOURNAMENT_STATUS, t.getStatus());
        int rows = db.update(TABLE_TOURNAMENTS, values, COL_TOURNAMENT_ID + "=?", new String[]{String.valueOf(t.getId())});
        db.close();
        return rows;
    }

    public boolean deleteTournament(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            String[] idArgs = new String[]{String.valueOf(id)};
            
            // Delete dependent records safely
            db.delete(TABLE_REGISTRATIONS, COL_REG_TID + "=?", idArgs);
            db.delete(TABLE_MATCHES, COL_MATCH_TID + "=?", idArgs);
            db.delete(TABLE_TEAMS, COL_TEAM_TID + "=?", idArgs);
            
            // Finally delete the tournament
            int rowsDeleted = db.delete(TABLE_TOURNAMENTS, COL_TOURNAMENT_ID + "=?", idArgs);
            
            if (rowsDeleted > 0) {
                db.setTransactionSuccessful();
                return true;
            }
        } finally {
            db.endTransaction();
            db.close();
        }
        return false;
    }

    // ==========================================
    // TOURNAMENT REGISTRATIONS CRUD (Phase 6A)
    // ==========================================

    public static final int REGISTRATION_SUCCESS = 1;
    public static final int REGISTRATION_DUPLICATE = -1;
    public static final int REGISTRATION_INELIGIBLE = -2;
    public static final int REGISTRATION_ERROR = -3;

    public int registerPlayerForTournament(String firebaseUid, int tournamentId, String registrationDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            // 1. Resolve local user ID
            User user = getUserByFirebaseUid(firebaseUid);
            if (user == null) {
                return REGISTRATION_ERROR;
            }
            int userId = user.getId();

            // 2. Validate Tournament Status is "Upcoming"
            Tournament tournament = getTournamentById(tournamentId);
            if (tournament == null || !"Upcoming".equals(tournament.getStatus())) {
                return REGISTRATION_INELIGIBLE;
            }

            // 3. Attempt Insert
            ContentValues values = new ContentValues();
            values.put(COL_REG_UID, userId);
            values.put(COL_REG_TID, tournamentId);
            values.put(COL_REG_DATE, registrationDate);

            long resultId = db.insertOrThrow(TABLE_REGISTRATIONS, null, values);
            if (resultId != -1) {
                db.setTransactionSuccessful();
                return REGISTRATION_SUCCESS;
            }
        } catch (android.database.sqlite.SQLiteConstraintException e) {
            // Catches UNIQUE constraint failure
            return REGISTRATION_DUPLICATE;
        } catch (Exception e) {
            return REGISTRATION_ERROR;
        } finally {
            db.endTransaction();
            // Do not close db here because getUserByFirebaseUid and getTournamentById handle their own db reads
            // and mixing readable/writable connections in SQLiteOpenHelper can cause issues if prematurely closed.
        }
        return REGISTRATION_ERROR;
    }

    public boolean isPlayerRegistered(String firebaseUid, int tournamentId) {
        User user = getUserByFirebaseUid(firebaseUid);
        if (user == null) return false;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_REGISTRATIONS, new String[]{COL_REG_ID},
                COL_REG_UID + "=? AND " + COL_REG_TID + "=?",
                new String[]{String.valueOf(user.getId()), String.valueOf(tournamentId)},
                null, null, null);

        boolean isRegistered = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) {
            cursor.close();
        }
        return isRegistered;
    }

    public List<Tournament> getPlayerRegistrations(String firebaseUid) {
        List<Tournament> registeredTournaments = new ArrayList<>();
        User user = getUserByFirebaseUid(firebaseUid);
        if (user == null) return registeredTournaments;

        SQLiteDatabase db = this.getReadableDatabase();
        
        // Inner join to get Tournament details for the user's registrations
        String query = "SELECT t.* FROM " + TABLE_TOURNAMENTS + " t " +
                "INNER JOIN " + TABLE_REGISTRATIONS + " r ON t." + COL_TOURNAMENT_ID + " = r." + COL_REG_TID + " " +
                "WHERE r." + COL_REG_UID + " = ? ORDER BY r." + COL_REG_DATE + " DESC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(user.getId())});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Tournament t = new Tournament();
                t.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_TOURNAMENT_ID)));
                t.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_TOURNAMENT_NAME)));
                t.setSport(cursor.getString(cursor.getColumnIndexOrThrow(COL_TOURNAMENT_SPORT)));
                t.setOrganizer(cursor.getString(cursor.getColumnIndexOrThrow(COL_TOURNAMENT_ORGANIZER)));
                t.setStartDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_TOURNAMENT_START)));
                t.setEndDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_TOURNAMENT_END)));
                t.setVenue(cursor.getString(cursor.getColumnIndexOrThrow(COL_TOURNAMENT_VENUE)));
                t.setMaxTeams(cursor.getInt(cursor.getColumnIndexOrThrow(COL_TOURNAMENT_MAX_TEAMS)));
                t.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COL_TOURNAMENT_STATUS)));
                registeredTournaments.add(t);
            } while (cursor.moveToNext());
        }
        
        if (cursor != null) {
            cursor.close();
        }
        return registeredTournaments;
    }

    // ==========================================
    // EVENTS CRUD (Phase 4)
    // ==========================================

    public long addEvent(Event e) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_EVENT_NAME, e.getName());
        values.put(COL_EVENT_DESC, e.getDescription());
        values.put(COL_EVENT_SPORT, e.getSport());
        values.put(COL_EVENT_DATE, e.getEventDate());
        values.put(COL_EVENT_VENUE, e.getVenue());
        values.put(COL_EVENT_ORGANIZER, e.getOrganizer());
        values.put(COL_EVENT_STATUS, e.getStatus());
        long id = db.insert(TABLE_EVENTS, null, values);
        db.close();
        return id;
    }

    public List<Event> getAllEvents() {
        List<Event> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_EVENTS, null);
        if (cursor.moveToFirst()) {
            do {
                Event e = new Event();
                e.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_EVENT_ID)));
                e.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_NAME)));
                e.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_DESC)));
                e.setSport(cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_SPORT)));
                e.setEventDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_DATE)));
                e.setVenue(cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_VENUE)));
                e.setOrganizer(cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_ORGANIZER)));
                e.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_STATUS)));
                list.add(e);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public Event getEventById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_EVENTS, null, COL_EVENT_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Event e = new Event();
            e.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_EVENT_ID)));
            e.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_NAME)));
            e.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_DESC)));
            e.setSport(cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_SPORT)));
            e.setEventDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_DATE)));
            e.setVenue(cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_VENUE)));
            e.setOrganizer(cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_ORGANIZER)));
            e.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_STATUS)));
            cursor.close();
            return e;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    public int updateEvent(Event e) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_EVENT_NAME, e.getName());
        values.put(COL_EVENT_DESC, e.getDescription());
        values.put(COL_EVENT_SPORT, e.getSport());
        values.put(COL_EVENT_DATE, e.getEventDate());
        values.put(COL_EVENT_VENUE, e.getVenue());
        values.put(COL_EVENT_ORGANIZER, e.getOrganizer());
        values.put(COL_EVENT_STATUS, e.getStatus());
        int rows = db.update(TABLE_EVENTS, values, COL_EVENT_ID + "=?", new String[]{String.valueOf(e.getId())});
        db.close();
        return rows;
    }

    public boolean deleteEvent(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_EVENTS, COL_EVENT_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rowsDeleted > 0;
    }

    // ==========================================
    // TEAM CRUD (Phase 6A)
    // ==========================================

    public long addTeam(com.example.sporthub.models.Team team) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TEAM_NAME, team.getName());
        values.put(COL_TEAM_SPORT, team.getSport());
        values.put(COL_TEAM_CAPTAIN, team.getCaptain());
        values.put(COL_TEAM_CONTACT, team.getContactEmail());
        values.put(COL_TEAM_TID, team.getTournamentId());
        long id = db.insert(TABLE_TEAMS, null, values);
        db.close();
        return id;
    }

    public List<com.example.sporthub.models.Team> getAllTeams() {
        List<com.example.sporthub.models.Team> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TEAMS, null);
        if (cursor.moveToFirst()) {
            do {
                com.example.sporthub.models.Team t = new com.example.sporthub.models.Team();
                t.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_TEAM_ID)));
                t.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_TEAM_NAME)));
                t.setSport(cursor.getString(cursor.getColumnIndexOrThrow(COL_TEAM_SPORT)));
                t.setCaptain(cursor.getString(cursor.getColumnIndexOrThrow(COL_TEAM_CAPTAIN)));
                t.setContactEmail(cursor.getString(cursor.getColumnIndexOrThrow(COL_TEAM_CONTACT)));
                t.setTournamentId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_TEAM_TID)));
                list.add(t);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public int updateTeam(com.example.sporthub.models.Team team) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TEAM_NAME, team.getName());
        values.put(COL_TEAM_SPORT, team.getSport());
        values.put(COL_TEAM_CAPTAIN, team.getCaptain());
        values.put(COL_TEAM_CONTACT, team.getContactEmail());
        values.put(COL_TEAM_TID, team.getTournamentId());
        int rows = db.update(TABLE_TEAMS, values, COL_TEAM_ID + "=?", new String[]{String.valueOf(team.getId())});
        db.close();
        return rows;
    }

    public void deleteTeam(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TEAMS, COL_TEAM_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    // ==========================================
    // MATCH CRUD (Phase 7)
    // ==========================================

    public List<com.example.sporthub.models.Team> getTeamsByTournamentId(int tournamentId) {
        List<com.example.sporthub.models.Team> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TEAMS, null, COL_TEAM_TID + "=?", new String[]{String.valueOf(tournamentId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                com.example.sporthub.models.Team t = new com.example.sporthub.models.Team();
                t.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_TEAM_ID)));
                t.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_TEAM_NAME)));
                t.setSport(cursor.getString(cursor.getColumnIndexOrThrow(COL_TEAM_SPORT)));
                t.setCaptain(cursor.getString(cursor.getColumnIndexOrThrow(COL_TEAM_CAPTAIN)));
                t.setContactEmail(cursor.getString(cursor.getColumnIndexOrThrow(COL_TEAM_CONTACT)));
                t.setTournamentId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_TEAM_TID)));
                list.add(t);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public long addMatch(com.example.sporthub.models.Match m) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_MATCH_TID, m.getTournamentId());
        values.put(COL_MATCH_TEAM_A, m.getTeamAId());
        values.put(COL_MATCH_TEAM_B, m.getTeamBId());
        values.put(COL_MATCH_DATE, m.getMatchDate());
        values.put(COL_MATCH_VENUE, m.getVenue());
        values.put(COL_MATCH_STATUS, m.getStatus());
        values.put(COL_MATCH_SCORE_A, m.getScoreA());
        values.put(COL_MATCH_SCORE_B, m.getScoreB());
        long id = db.insert(TABLE_MATCHES, null, values);
        db.close();
        return id;
    }

    public List<com.example.sporthub.models.Match> getAllMatches() {
        List<com.example.sporthub.models.Match> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        // Inner join to get the names of the teams and tournament for easy UI display
        String query = "SELECT m.*, t1." + COL_TEAM_NAME + " AS teamA_name, t2." + COL_TEAM_NAME + " AS teamB_name, trn." + COL_TOURNAMENT_NAME + " AS tournament_name " +
                "FROM " + TABLE_MATCHES + " m " +
                "INNER JOIN " + TABLE_TEAMS + " t1 ON m." + COL_MATCH_TEAM_A + " = t1." + COL_TEAM_ID + " " +
                "INNER JOIN " + TABLE_TEAMS + " t2 ON m." + COL_MATCH_TEAM_B + " = t2." + COL_TEAM_ID + " " +
                "INNER JOIN " + TABLE_TOURNAMENTS + " trn ON m." + COL_MATCH_TID + " = trn." + COL_TOURNAMENT_ID + " " +
                "ORDER BY m." + COL_MATCH_DATE + " DESC";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                com.example.sporthub.models.Match m = new com.example.sporthub.models.Match();
                m.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_MATCH_ID)));
                m.setTournamentId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_MATCH_TID)));
                m.setTeamAId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_MATCH_TEAM_A)));
                m.setTeamBId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_MATCH_TEAM_B)));
                m.setMatchDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_MATCH_DATE)));
                m.setVenue(cursor.getString(cursor.getColumnIndexOrThrow(COL_MATCH_VENUE)));
                m.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COL_MATCH_STATUS)));
                m.setScoreA(cursor.getInt(cursor.getColumnIndexOrThrow(COL_MATCH_SCORE_A)));
                m.setScoreB(cursor.getInt(cursor.getColumnIndexOrThrow(COL_MATCH_SCORE_B)));
                
                m.setTeamAName(cursor.getString(cursor.getColumnIndexOrThrow("teamA_name")));
                m.setTeamBName(cursor.getString(cursor.getColumnIndexOrThrow("teamB_name")));
                m.setTournamentName(cursor.getString(cursor.getColumnIndexOrThrow("tournament_name")));
                
                list.add(m);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public int updateMatch(com.example.sporthub.models.Match m) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_MATCH_TID, m.getTournamentId());
        values.put(COL_MATCH_TEAM_A, m.getTeamAId());
        values.put(COL_MATCH_TEAM_B, m.getTeamBId());
        values.put(COL_MATCH_DATE, m.getMatchDate());
        values.put(COL_MATCH_VENUE, m.getVenue());
        values.put(COL_MATCH_STATUS, m.getStatus());
        values.put(COL_MATCH_SCORE_A, m.getScoreA());
        values.put(COL_MATCH_SCORE_B, m.getScoreB());
        int rows = db.update(TABLE_MATCHES, values, COL_MATCH_ID + "=?", new String[]{String.valueOf(m.getId())});
        db.close();
        return rows;
    }

    public void deleteMatch(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MATCHES, COL_MATCH_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    // ==========================================
    // FITNESS TABLE (Phase 9)
    // ==========================================

    public long addFitnessRecord(int userId, String activityType, int durationMins, double calories, String recordDate, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FITNESS_UID, userId);
        values.put(COL_FITNESS_ACTIVITY, activityType);
        values.put(COL_FITNESS_DURATION, durationMins);
        values.put(COL_FITNESS_CALORIES, calories);
        values.put(COL_FITNESS_DATE, recordDate);
        values.put(COL_FITNESS_NOTES, notes);
        long id = db.insert(TABLE_FITNESS, null, values);
        db.close();
        return id;
    }

    public long addFitnessRecord(com.example.sporthub.models.FitnessRecord r) {
        return addFitnessRecord(r.getUserId(), r.getActivityType(), r.getDurationMinutes(), r.getCaloriesBurned(), r.getRecordDate(), r.getNotes());
    }

    public List<com.example.sporthub.models.FitnessRecord> getFitnessRecordsByUserId(int userId) {
        List<com.example.sporthub.models.FitnessRecord> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FITNESS, null, COL_FITNESS_UID + "=?", new String[]{String.valueOf(userId)}, null, null, COL_FITNESS_DATE + " DESC");
        
        if (cursor.moveToFirst()) {
            do {
                com.example.sporthub.models.FitnessRecord r = new com.example.sporthub.models.FitnessRecord();
                r.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_FITNESS_ID)));
                r.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_FITNESS_UID)));
                r.setActivityType(cursor.getString(cursor.getColumnIndexOrThrow(COL_FITNESS_ACTIVITY)));
                r.setDurationMinutes(cursor.getInt(cursor.getColumnIndexOrThrow(COL_FITNESS_DURATION)));
                r.setCaloriesBurned(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_FITNESS_CALORIES)));
                r.setRecordDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_FITNESS_DATE)));
                r.setNotes(cursor.getString(cursor.getColumnIndexOrThrow(COL_FITNESS_NOTES)));
                list.add(r);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public com.example.sporthub.models.FitnessRecord getFitnessRecordByIdAndUser(int recordId, int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FITNESS, null, 
                COL_FITNESS_ID + "=? AND " + COL_FITNESS_UID + "=?", 
                new String[]{String.valueOf(recordId), String.valueOf(userId)}, 
                null, null, null);
                
        if (cursor != null && cursor.moveToFirst()) {
            com.example.sporthub.models.FitnessRecord r = new com.example.sporthub.models.FitnessRecord();
            r.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_FITNESS_ID)));
            r.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_FITNESS_UID)));
            r.setActivityType(cursor.getString(cursor.getColumnIndexOrThrow(COL_FITNESS_ACTIVITY)));
            r.setDurationMinutes(cursor.getInt(cursor.getColumnIndexOrThrow(COL_FITNESS_DURATION)));
            r.setCaloriesBurned(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_FITNESS_CALORIES)));
            r.setRecordDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_FITNESS_DATE)));
            r.setNotes(cursor.getString(cursor.getColumnIndexOrThrow(COL_FITNESS_NOTES)));
            cursor.close();
            return r;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    public int updateFitnessRecord(com.example.sporthub.models.FitnessRecord r) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FITNESS_ACTIVITY, r.getActivityType());
        values.put(COL_FITNESS_DURATION, r.getDurationMinutes());
        values.put(COL_FITNESS_CALORIES, r.getCaloriesBurned());
        values.put(COL_FITNESS_DATE, r.getRecordDate());
        values.put(COL_FITNESS_NOTES, r.getNotes());
        
        // Ensure update only affects records owned by the calling user
        int rows = db.update(TABLE_FITNESS, values, 
                COL_FITNESS_ID + "=? AND " + COL_FITNESS_UID + "=?", 
                new String[]{String.valueOf(r.getId()), String.valueOf(r.getUserId())});
        db.close();
        return rows;
    }

    public boolean deleteFitnessRecord(int recordId, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_FITNESS, 
                COL_FITNESS_ID + "=? AND " + COL_FITNESS_UID + "=?", 
                new String[]{String.valueOf(recordId), String.valueOf(userId)});
        db.close();
        return rows > 0;
    }

    // ==========================================
    // WORKOUT PLANNER CRUD (Phase 10)
    // ==========================================

    public long addWorkoutPlan(com.example.sporthub.models.WorkoutPlan wp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_WORKOUT_UID, wp.getUserId());
        values.put(COL_WORKOUT_TITLE, wp.getTitle());
        values.put(COL_WORKOUT_ACTIVITY, wp.getActivityType());
        values.put(COL_WORKOUT_DATE, wp.getPlannedDate());
        values.put(COL_WORKOUT_DURATION, wp.getTargetDuration());
        values.put(COL_WORKOUT_NOTES, wp.getNotes());
        long id = db.insert(TABLE_WORKOUTS, null, values);
        db.close();
        return id;
    }

    public List<com.example.sporthub.models.WorkoutPlan> getWorkoutPlansByUserId(int userId) {
        List<com.example.sporthub.models.WorkoutPlan> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_WORKOUTS, null, COL_WORKOUT_UID + "=?", new String[]{String.valueOf(userId)}, null, null, COL_WORKOUT_DATE + " ASC");
        
        if (cursor.moveToFirst()) {
            do {
                com.example.sporthub.models.WorkoutPlan wp = new com.example.sporthub.models.WorkoutPlan();
                wp.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_WORKOUT_ID)));
                wp.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_WORKOUT_UID)));
                wp.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COL_WORKOUT_TITLE)));
                wp.setActivityType(cursor.getString(cursor.getColumnIndexOrThrow(COL_WORKOUT_ACTIVITY)));
                wp.setPlannedDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_WORKOUT_DATE)));
                wp.setTargetDuration(cursor.getInt(cursor.getColumnIndexOrThrow(COL_WORKOUT_DURATION)));
                wp.setNotes(cursor.getString(cursor.getColumnIndexOrThrow(COL_WORKOUT_NOTES)));
                list.add(wp);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public com.example.sporthub.models.WorkoutPlan getWorkoutPlanByIdAndUser(int planId, int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_WORKOUTS, null, 
                COL_WORKOUT_ID + "=? AND " + COL_WORKOUT_UID + "=?", 
                new String[]{String.valueOf(planId), String.valueOf(userId)}, 
                null, null, null);
                
        if (cursor != null && cursor.moveToFirst()) {
            com.example.sporthub.models.WorkoutPlan wp = new com.example.sporthub.models.WorkoutPlan();
            wp.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_WORKOUT_ID)));
            wp.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_WORKOUT_UID)));
            wp.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COL_WORKOUT_TITLE)));
            wp.setActivityType(cursor.getString(cursor.getColumnIndexOrThrow(COL_WORKOUT_ACTIVITY)));
            wp.setPlannedDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_WORKOUT_DATE)));
            wp.setTargetDuration(cursor.getInt(cursor.getColumnIndexOrThrow(COL_WORKOUT_DURATION)));
            wp.setNotes(cursor.getString(cursor.getColumnIndexOrThrow(COL_WORKOUT_NOTES)));
            cursor.close();
            return wp;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    public int updateWorkoutPlan(com.example.sporthub.models.WorkoutPlan wp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_WORKOUT_TITLE, wp.getTitle());
        values.put(COL_WORKOUT_ACTIVITY, wp.getActivityType());
        values.put(COL_WORKOUT_DATE, wp.getPlannedDate());
        values.put(COL_WORKOUT_DURATION, wp.getTargetDuration());
        values.put(COL_WORKOUT_NOTES, wp.getNotes());
        
        int rows = db.update(TABLE_WORKOUTS, values, 
                COL_WORKOUT_ID + "=? AND " + COL_WORKOUT_UID + "=?", 
                new String[]{String.valueOf(wp.getId()), String.valueOf(wp.getUserId())});
        db.close();
        return rows;
    }

    public boolean deleteWorkoutPlan(int planId, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_WORKOUTS, 
                COL_WORKOUT_ID + "=? AND " + COL_WORKOUT_UID + "=?", 
                new String[]{String.valueOf(planId), String.valueOf(userId)});
        db.close();
        return rows > 0;
    }

}