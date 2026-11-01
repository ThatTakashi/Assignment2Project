package com.example.assignment2project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class TournamentAdapter extends ArrayAdapter<Tournament> {

    public interface OnActionListener {
        void onEdit(Tournament tournament);
        void onDelete(Tournament tournament);
    }

    private final OnActionListener listener;

    public TournamentAdapter(Context context, List<Tournament> tournaments, OnActionListener listener) {
        super(context, 0, tournaments);
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tournament, parent, false);
        }

        Tournament t = getItem(position);
        if (t != null) {
            TextView tvName = convertView.findViewById(R.id.tv_name);
            TextView tvDetails = convertView.findViewById(R.id.tv_details);
            TextView tvDates = convertView.findViewById(R.id.tv_dates);
            TextView tvRating = convertView.findViewById(R.id.tv_rating);
            Button btnEdit = convertView.findViewById(R.id.btn_edit);
            Button btnDelete = convertView.findViewById(R.id.btn_delete);

            tvName.setText(t.getName());
            tvDetails.setText("Category: " + t.getCategory() + " | Difficulty: " + t.getDifficulty());
            tvDates.setText("Dates: " + t.getStartDate() + " to " + t.getEndDate());
            tvRating.setText("Rating: " + t.getRating() + " / 5");

            btnEdit.setOnClickListener(v -> listener.onEdit(t));
            btnDelete.setOnClickListener(v -> listener.onDelete(t));
        }

        return convertView;
    }
}
