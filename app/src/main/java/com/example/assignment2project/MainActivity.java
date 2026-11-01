package com.example.assignment2project;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TournamentAdapter.OnActionListener {

    private ListView lvTournaments;
    private TextView tvEmpty;
    private DatabaseHelper dbHelper;
    private TournamentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create a new databasehelper
        dbHelper = new DatabaseHelper(this);

        lvTournaments = findViewById(R.id.lv_tournaments);
        tvEmpty = findViewById(R.id.tv_empty);
        Button btnAdd = findViewById(R.id.btn_add);
        Button btnLogout = findViewById(R.id.btn_logout);

        btnAdd.setOnClickListener(v -> startActivity(new Intent(this, CreateTournamentActivity.class)));

        btnLogout.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        // Link each listItem with the tournament details activity
        lvTournaments.setOnItemClickListener((parent, view, position, id) -> {
            Tournament t = adapter.getItem(position);
            if (t != null) {
                Intent intent = new Intent(this, TournamentDetailActivity.class);
                intent.putExtra("extra_tournament_id", t.getId());
                startActivity(intent);
            }
        });
    }

    // reload all the tournaments if the app is resumed from the background
    @Override
    protected void onResume() {
        super.onResume();
        loadTournaments();
    }

    // Load all the tournaments from the database into the main listView
    private void loadTournaments() {
        List<Tournament> list = dbHelper.getAllTournaments();
        if (list.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            lvTournaments.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            lvTournaments.setVisibility(View.VISIBLE);
            adapter = new TournamentAdapter(this, list, this);
            lvTournaments.setAdapter(adapter);
        }
    }

    @Override
    public void onEdit(Tournament tournament) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_tournament, null);
        builder.setView(dialogView);

        EditText etName = dialogView.findViewById(R.id.et_edit_name);
        EditText etStartDate = dialogView.findViewById(R.id.et_edit_start_date);
        EditText etEndDate = dialogView.findViewById(R.id.et_edit_end_date);

        etName.setText(tournament.getName());
        etStartDate.setText(tournament.getStartDate());
        etEndDate.setText(tournament.getEndDate());

        etStartDate.setOnClickListener(v -> pickDate(etStartDate));
        etEndDate.setOnClickListener(v -> pickDate(etEndDate));

        builder.setPositiveButton("Update", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String start = etStartDate.getText().toString().trim();
            String end = etEndDate.getText().toString().trim();

            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(start) && !TextUtils.isEmpty(end)) {
                dbHelper.updateTournament(tournament.getId(), name, start, end);
                Toast.makeText(this, "Tournament updated", Toast.LENGTH_SHORT).show();
                loadTournaments();
            } else {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    @Override
    public void onDelete(Tournament tournament) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Tournament")
                .setMessage("Are you sure you want to delete '" + tournament.getName() + "'?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteTournament(tournament.getId());
                    Toast.makeText(this, "Tournament deleted", Toast.LENGTH_SHORT).show();
                    loadTournaments();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void pickDate(EditText target) {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String dateStr = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            target.setText(dateStr);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }
}
