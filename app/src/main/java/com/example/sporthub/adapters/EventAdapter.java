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
            tvStatus.setText(event.getStatus());
        }

        return convertView;
    }
}