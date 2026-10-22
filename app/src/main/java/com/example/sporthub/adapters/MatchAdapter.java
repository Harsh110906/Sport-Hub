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
import com.example.sporthub.models.Match;

import java.util.List;

public class MatchAdapter extends ArrayAdapter<Match> {

    public MatchAdapter(@NonNull Context context, @NonNull List<Match> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_match, parent, false);
        }

        Match match = getItem(position);

        TextView tvTournament = convertView.findViewById(R.id.tvMatchTournament);
        TextView tvTeams = convertView.findViewById(R.id.tvMatchTeams);
        TextView tvDateVenue = convertView.findViewById(R.id.tvMatchDateVenue);
        TextView tvStatus = convertView.findViewById(R.id.tvMatchStatus);
        TextView tvScore = convertView.findViewById(R.id.tvMatchScore);

        if (match != null) {
            tvTournament.setText(match.getTournamentName());
            tvTeams.setText(match.getTeamAName() + " vs " + match.getTeamBName());
            tvDateVenue.setText(match.getMatchDate() + " | " + match.getVenue());
            
            String status = match.getStatus() != null ? match.getStatus() : "Unknown";
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

            if ("Completed".equalsIgnoreCase(match.getStatus())) {
                tvScore.setVisibility(View.VISIBLE);
                tvScore.setText("Score: " + match.getScoreA() + " - " + match.getScoreB());
            } else {
                tvScore.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    public void updateData(List<Match> newMatches) {
        clear();
        if (newMatches != null) {
            addAll(newMatches);
        }
        notifyDataSetChanged();
    }
}