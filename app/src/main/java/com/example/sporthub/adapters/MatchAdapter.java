package com.example.sporthub.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
            tvStatus.setText(match.getStatus());

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