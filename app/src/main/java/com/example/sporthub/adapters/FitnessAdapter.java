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
import com.example.sporthub.models.FitnessRecord;

import java.util.List;

public class FitnessAdapter extends ArrayAdapter<FitnessRecord> {

    public FitnessAdapter(@NonNull Context context, @NonNull List<FitnessRecord> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_fitness, parent, false);
        }

        FitnessRecord record = getItem(position);

        TextView tvActivity = convertView.findViewById(R.id.tvFitnessActivity);
        TextView tvDuration = convertView.findViewById(R.id.tvFitnessDuration);
        TextView tvCalories = convertView.findViewById(R.id.tvFitnessCalories);
        TextView tvDate = convertView.findViewById(R.id.tvFitnessDate);

        if (record != null) {
            tvActivity.setText(record.getActivityType());
            tvDuration.setText(record.getDurationMinutes() + " mins");
            tvCalories.setText(String.format("%.1f kcal", record.getCaloriesBurned()));
            tvDate.setText("Recorded on: " + record.getRecordDate());
        }

        return convertView;
    }
}