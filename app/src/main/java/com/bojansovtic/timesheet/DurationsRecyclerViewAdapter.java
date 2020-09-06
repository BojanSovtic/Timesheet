package com.bojansovtic.timesheet;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;

import java.util.Locale;

public class DurationsRecyclerViewAdapter extends RecyclerView.Adapter<DurationsRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "DurationsRecyclerViewAd";

    private Cursor cursor;

    private final java.text.DateFormat dateFormat;

    public DurationsRecyclerViewAdapter(Context context, Cursor cursor) {
        this.cursor = cursor;
        dateFormat = DateFormat.getDateFormat(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_durations_items, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (cursor != null && cursor.getCount() != 0) {
            if (!cursor.moveToPosition(position)) {
                throw new IllegalStateException("Couldn't move cursor to position " + position);
            }
            String name = cursor.getString(cursor.getColumnIndex(DurationsContract.Columns.DURATIONS_NAME));
            String description = cursor.getString(cursor.getColumnIndex(DurationsContract.Columns.DURATIONS_DESCRIPTION));
            long startTime = cursor.getLong(cursor.getColumnIndex(DurationsContract.Columns.DURATIONS_START_TIME));
            long totalDuration = cursor.getLong(cursor.getColumnIndex(DurationsContract.Columns.DURATIONS_DURATION));

            holder.name.setText(name);
            if (holder.description != null) {
                holder.description.setText(description);
            }

            String userDate = dateFormat.format(startTime * 1000);
            String totalTime = formatDuration(totalDuration);

            holder.startDate.setText(userDate);
            holder.duration.setText(totalTime);
        }
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    private String formatDuration(long duration) {
        long hours = duration / 3600;
        long remaining = duration - (hours * 3600);
        long minutes = remaining / 60;
        long seconds = remaining - (minutes * 60);

        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);
    }

    Cursor swapCursor(Cursor newCursor) {
        if (newCursor == cursor) {
            return null;
        }

        int count = getItemCount();

        final Cursor oldCursor = cursor;
        cursor = newCursor;
        if (newCursor != null) {
            notifyDataSetChanged();
        } else {
            notifyItemRangeRemoved(0, count);
        }
        return oldCursor;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView description;
        TextView startDate;
        TextView duration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.td_name);
            this.description = itemView.findViewById(R.id.td_description);
            this.startDate = itemView.findViewById(R.id.td_start);
            this.duration = itemView.findViewById(R.id.td_duration);
        }
    }
}
