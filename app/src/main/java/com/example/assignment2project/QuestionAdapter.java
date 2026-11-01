package com.example.assignment2project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class QuestionAdapter extends ArrayAdapter<Question> {

    public QuestionAdapter(Context context, List<Question> questions) {
        super(context, 0, questions);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_question, parent, false);
        }

        Question q = getItem(position);
        if (q != null) {
            TextView tvTitle = convertView.findViewById(R.id.tv_question_title);
            TextView tvCorrect = convertView.findViewById(R.id.tv_correct);
            TextView tvIncorrect = convertView.findViewById(R.id.tv_incorrect);

            tvTitle.setText("Q" + (position + 1) + ": " + q.getQuestion());
            tvCorrect.setText("Correct: " + q.getCorrectAnswer());

            if (q.getIncorrectAnswers() != null && !q.getIncorrectAnswers().isEmpty()) {
                tvIncorrect.setText("Incorrect: " + String.join(", ", q.getIncorrectAnswers()));
                tvIncorrect.setVisibility(View.VISIBLE);
            } else {
                tvIncorrect.setVisibility(View.GONE);
            }
        }

        return convertView;
    }
}
