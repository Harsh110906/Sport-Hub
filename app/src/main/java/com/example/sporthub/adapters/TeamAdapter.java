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
import com.example.sporthub.models.Team;

import java.util.List;

public class TeamAdapter extends ArrayAdapter<Team> {

    public TeamAdapter(@NonNull Context context, @NonNull List<Team> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_team, parent, false);
        }

        Team team = getItem(position);

        TextView tvName = convertView.findViewById(R.id.tvTeamName);
        TextView tvSport = convertView.findViewById(R.id.tvTeamSport);
        TextView tvCaptain = convertView.findViewById(R.id.tvTeamCaptain);

        if (team != null) {
            tvName.setText(team.getName());
            tvSport.setText("Sport: " + team.getSport());
            tvCaptain.setText("Captain: " + team.getCaptain());
        }

        return convertView;
    }
}