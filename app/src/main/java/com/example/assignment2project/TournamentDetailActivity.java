package com.example.assignment2project;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class TournamentDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament_detail);

        DatabaseHelper dbHelper = new DatabaseHelper(this);

        TextView tvName = findViewById(R.id.tv_detail_name);
        TextView tvInfo = findViewById(R.id.tv_detail_info);
        TextView tvDates = findViewById(R.id.tv_detail_dates);
        TextView tvRating = findViewById(R.id.tv_detail_rating);
        ListView lvQuestions = findViewById(R.id.lv_questions);

        long tournamentId = getIntent().getLongExtra("extra_tournament_id", -1);
        Tournament t = dbHelper.getTournamentById(tournamentId);

        if (t != null) {
            tvName.setText(t.getName());
            tvInfo.setText("Category: " + t.getCategory() + " | Difficulty: " + t.getDifficulty());
            tvDates.setText("Dates: " + t.getStartDate() + " to " + t.getEndDate());
            tvRating.setText("Rating: " + t.getRating() + " / 5.0");

            List<Question> questions = dbHelper.getQuestionsForTournament(tournamentId);
            lvQuestions.setAdapter(new QuestionAdapter(this, questions));
        } else {
            Toast.makeText(this, "Tournament not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
