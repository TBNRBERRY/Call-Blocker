package com.example.callblocker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class CallLogAdapter extends ArrayAdapter<CallLogEntry> {

    public CallLogAdapter(Context context, List<CallLogEntry> logs) {
        super(context, 0, logs);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        CallLogEntry entry = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.call_log_item, parent, false);
        }

        TextView callerNameTextView = convertView.findViewById(R.id.callerNameTextView);
        TextView callDetailsTextView = convertView.findViewById(R.id.callDetailsTextView);

        assert entry != null;
        callerNameTextView.setText(entry.getDisplayName());
        callDetailsTextView.setText(entry.getFormattedDetails());

        return convertView;
    }
}

