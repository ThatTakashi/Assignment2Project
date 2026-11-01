package com.example.assignment2project;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateTournamentActivity extends AppCompatActivity {

    private EditText etName, etStartDate, etEndDate;
    private Spinner spinnerCategory, spinnerDifficulty, spinnerType;
    private RatingBar ratingBarInput;
    private ProgressBar progressBar;
    private Button btnCreate;
    private DatabaseHelper dbHelper;

    private final String[] categoryNames = {
            "Any Category", "General Knowledge", "Books", "Film", "Music",
            "Science & Nature", "Computers", "Mathematics", "Sports", "Geography", "History"
    };

    private final Integer[] categoryIds = {null, 9, 10, 11, 12, 17, 18, 19, 21, 22, 23};
    private final String[] difficultyNames = {"Any Difficulty", "Easy", "Medium", "Hard"};
    private final String[] difficultyValues = {null, "easy", "medium", "hard"};
    private final String[] typeNames = {"Any Type (Mixture)", "Multiple Choice", "True / False"};
    private final String[] typeValues = {null, "multiple", "boolean"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tournament);

        // Create a new database helper (and database if it doesn't exist)
        dbHelper = new DatabaseHelper(this);

        // View references
        etName = findViewById(R.id.et_name);
        etStartDate = findViewById(R.id.et_start_date);
        etEndDate = findViewById(R.id.et_end_date);
        spinnerCategory = findViewById(R.id.spinner_category);
        spinnerDifficulty = findViewById(R.id.spinner_difficulty);
        spinnerType = findViewById(R.id.spinner_type);
        ratingBarInput = findViewById(R.id.rating_bar_input);
        progressBar = findViewById(R.id.progress_bar);
        btnCreate = findViewById(R.id.btn_create);

        // Setup for the sppinner views
        spinnerCategory.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categoryNames));
        spinnerDifficulty.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, difficultyNames));
        spinnerType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, typeNames));

        // Setup for the date pickers
        etStartDate.setOnClickListener(v -> pickDate(etStartDate));
        etEndDate.setOnClickListener(v -> pickDate(etEndDate));

        // Create button onclick listener
        btnCreate.setOnClickListener(v -> createTournament());
    }

    // Open a new date picker dialogue
    private void pickDate(EditText target) {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String dateStr = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            target.setText(dateStr);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    // Create tournament method
    private void createTournament() {
        // Get tournament details from the UI
        String name = etName.getText().toString().trim();
        String start = etStartDate.getText().toString().trim();
        String end = etEndDate.getText().toString().trim();

        // Check if any of the fields are emtpy
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(start) || TextUtils.isEmpty(end)) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int catPos = spinnerCategory.getSelectedItemPosition();
        int diffPos = spinnerDifficulty.getSelectedItemPosition();
        int typePos = spinnerType.getSelectedItemPosition();

        // Stop the user from clicking the create button multiple times
        progressBar.setVisibility(View.VISIBLE);
        btnCreate.setEnabled(false);

        // API call to create the fetch 10 questions and create the tournament
        ApiClient.getApi().getQuestions(10, categoryIds[catPos], difficultyValues[diffPos], typeValues[typePos])
                .enqueue(new Callback<OpenTdbResponse>() {
                    @Override
                    public void onResponse(Call<OpenTdbResponse> call, Response<OpenTdbResponse> response) {
                        progressBar.setVisibility(View.GONE);
                        btnCreate.setEnabled(true);

                        if (response.isSuccessful() && response.body() != null && response.body().getResults() != null) {
                            // Check if any questions were returned
                            // Add them to a new list of QuestionItems so they can be parsed
                            List<QuestionItem> items = response.body().getResults();
                            if (items.isEmpty()) {
                                Toast.makeText(CreateTournamentActivity.this, "No questions returned", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Create a new tournament with the questions received
                            Tournament t = new Tournament(0, name, categoryNames[catPos], difficultyNames[diffPos], start, end, ratingBarInput.getRating());
                            dbHelper.insertTournament(t, items);
                            Toast.makeText(CreateTournamentActivity.this, "Tournament Created with 10 questions!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            // Tell the user if the request fails to get a response
                            Toast.makeText(CreateTournamentActivity.this, "Failed to fetch questions from OpenTDB, Please try again", Toast.LENGTH_SHORT).show();
                        }
                    }

                    // Tell the user if the request fails to get a response
                    // Enable the create button again
                    @Override
                    public void onFailure(Call<OpenTdbResponse> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        btnCreate.setEnabled(true);
                        Toast.makeText(CreateTournamentActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
