package com.example.sporthub.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.sporthub.R;
import com.example.sporthub.models.Tournament;

import java.util.List;

public class TournamentAdapter extends ArrayAdapter<Tournament> {

    public TournamentAdapter(@NonNull Context context, @NonNull List<Tournament> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tournament, parent, false);
        }

        Tournament tournament = getItem(position);

        TextView tvName = convertView.findViewById(R.id.tvTournamentName);
        TextView tvSport = convertView.findViewById(R.id.tvTournamentSport);
        TextView tvStatus = convertView.findViewById(R.id.tvTournamentStatus);

        if (tournament != null) {
            tvName.setText(tournament.getName());
            tvSport.setText(tournament.getSport());
            
            String status = tournament.getStatus() != null ? tournament.getStatus() : "Unknown";
            tvStatus.setText(status);

            int padStart = tvStatus.getPaddingStart();
            int padTop = tvStatus.getPaddingTop();
            int padEnd = tvStatus.getPaddingEnd();
            int padBottom = tvStatus.getPaddingBottom();

            if ("Completed".equalsIgnoreCase(status)) {
                tvStatus.setBackgroundResource(R.drawable.bg_status_completed);
                tvStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.status_text_completed));
            } else if ("Ongoing".equalsIgnoreCase(status)) {
                tvStatus.setBackgroundResource(R.drawable.bg_status_ongoing);
                tvStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.status_text_ongoing));
            } else if ("Upcoming".equalsIgnoreCase(status) || "Scheduled".equalsIgnoreCase(status)) {
                tvStatus.setBackgroundResource(R.drawable.bg_status_scheduled);
                tvStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.status_text_scheduled));
            } else if ("Cancelled".equalsIgnoreCase(status)) {
                tvStatus.setBackgroundResource(R.drawable.bg_status_cancelled);
                tvStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.status_text_cancelled));
            } else {
                tvStatus.setBackgroundResource(R.drawable.bg_status_neutral);
                tvStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.text_on_primary));
            }
            
            tvStatus.setPaddingRelative(padStart, padTop, padEnd, padBottom);
        }

        return convertView;
    }

    public void updateData(List<Tournament> newTournaments) {
        clear();
        if (newTournaments != null) {
            addAll(newTournaments);
        }
        notifyDataSetChanged();
    }
}