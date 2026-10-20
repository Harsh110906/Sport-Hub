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
            tvStatus.setText(tournament.getStatus());
        }

        return convertView;
    }
}