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
import com.example.sporthub.models.WorkoutPlan;

import java.util.List;

public class WorkoutAdapter extends ArrayAdapter<WorkoutPlan> {

    public WorkoutAdapter(@NonNull Context context, @NonNull List<WorkoutPlan> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_workout, parent, false);
        }

        WorkoutPlan plan = getItem(position);

        TextView tvTitle = convertView.findViewById(R.id.tvWorkoutTitle);
        TextView tvActivity = convertView.findViewById(R.id.tvWorkoutActivity);
        TextView tvDuration = convertView.findViewById(R.id.tvWorkoutDuration);
        TextView tvDate = convertView.findViewById(R.id.tvWorkoutDate);

        if (plan != null) {
            tvTitle.setText(plan.getTitle());
            tvActivity.setText(plan.getActivityType());
            tvDuration.setText(plan.getTargetDuration() + " mins");
            tvDate.setText("Planned for: " + plan.getPlannedDate());
        }

        return convertView;
    }
}