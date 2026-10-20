package com.example.sporthub.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sporthub.R;
import com.example.sporthub.database.DatabaseHelper;
import com.example.sporthub.models.Tournament;

import java.util.List;

public class TournamentDemoActivity extends AppCompatActivity {
    private EditText etName, etSport, etVenue;
    private Button btnAdd, btnUpdate, btnDelete, btnClear;
    private ListView lvTournaments;
    private DatabaseHelper dbHelper;
    private List<Tournament> tournamentList;
    private ArrayAdapter<Tournament> adapter;
    private Tournament selectedTournament = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament_demo);

        dbHelper = new DatabaseHelper(this);

        etName = findViewById(R.id.etName);
        etSport = findViewById(R.id.etSport);
        etVenue = findViewById(R.id.etVenue);
        btnAdd = findViewById(R.id.btnAdd);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        btnClear = findViewById(R.id.btnClear);
        lvTournaments = findViewById(R.id.lvTournaments);

        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);

        loadTournaments();

        btnAdd.setOnClickListener(v -> addTournament());
        btnUpdate.setOnClickListener(v -> updateTournament());
        btnDelete.setOnClickListener(v -> confirmDelete());
        btnClear.setOnClickListener(v -> clearSelection());

        lvTournaments.setOnItemClickListener((parent, view, position, id) -> {
            selectedTournament = tournamentList.get(position);
            etName.setText(selectedTournament.getName());
            etSport.setText(selectedTournament.getSport());
            etVenue.setText(selectedTournament.getVenue());
            btnAdd.setEnabled(false);
            btnUpdate.setEnabled(true);
            btnDelete.setEnabled(true);
        });
    }

    private void loadTournaments() {
        tournamentList = dbHelper.getAllTournaments();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tournamentList);
        lvTournaments.setAdapter(adapter);
    }

    private void addTournament() {
        String name = etName.getText().toString().trim();
        String sport = etSport.getText().toString().trim();
        String venue = etVenue.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(sport)) {
            Toast.makeText(this, "Name and Sport are required", Toast.LENGTH_SHORT).show();
            return;
        }

        Tournament t = new Tournament();
        t.setName(name);
        t.setSport(sport);
        t.setVenue(venue);
        t.setStatus("Upcoming"); // Default

        long id = dbHelper.addTournament(t);
        if (id != -1) {
            Toast.makeText(this, "Tournament Added!", Toast.LENGTH_SHORT).show();
            clearSelection();
            loadTournaments();
        } else {
            Toast.makeText(this, "Error adding tournament", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateTournament() {
        if (selectedTournament == null) return;
        
        String name = etName.getText().toString().trim();
        String sport = etSport.getText().toString().trim();
        String venue = etVenue.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(sport)) {
            Toast.makeText(this, "Name and Sport are required", Toast.LENGTH_SHORT).show();
            return;
        }

        selectedTournament.setName(name);
        selectedTournament.setSport(sport);
        selectedTournament.setVenue(venue);

        int rows = dbHelper.updateTournament(selectedTournament);
        if (rows > 0) {
            Toast.makeText(this, "Tournament Updated!", Toast.LENGTH_SHORT).show();
            clearSelection();
            loadTournaments();
        } else {
            Toast.makeText(this, "Error updating", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDelete() {
        if (selectedTournament == null) return;
        new AlertDialog.Builder(this)
            .setTitle("Delete Tournament")
            .setMessage("Are you sure you want to delete " + selectedTournament.getName() + "?")
            .setPositiveButton("Yes", (dialog, which) -> {
                dbHelper.deleteTournament(selectedTournament.getId());
                Toast.makeText(this, "Tournament Deleted", Toast.LENGTH_SHORT).show();
                clearSelection();
                loadTournaments();
            })
            .setNegativeButton("No", null)
            .show();
    }

    private void clearSelection() {
        selectedTournament = null;
        etName.setText("");
        etSport.setText("");
        etVenue.setText("");
        btnAdd.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }
}