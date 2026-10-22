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
import com.example.sporthub.models.Event;

import java.util.List;

public class EventAdapter extends ArrayAdapter<Event> {

    public EventAdapter(@NonNull Context context, @NonNull List<Event> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_event, parent, false);
        }

        Event event = getItem(position);

        TextView tvName = convertView.findViewById(R.id.tvEventName);
        TextView tvSport = convertView.findViewById(R.id.tvEventSport);
        TextView tvDate = convertView.findViewById(R.id.tvEventDate);
        TextView tvStatus = convertView.findViewById(R.id.tvEventStatus);

        if (event != null) {
            tvName.setText(event.getName());
            tvSport.setText("Sport: " + event.getSport());
            tvDate.setText("Date: " + event.getEventDate());
            
            String status = event.getStatus() != null ? event.getStatus() : "Unknown";
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
}