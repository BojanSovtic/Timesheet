package com.bojansovtic.timesheet;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class CursorRecyclerViewAdapter extends RecyclerView.Adapter<CursorRecyclerViewAdapter.TaskViewHolder> {
    private Cursor cursor;
    private OnTaskClickListener listener;

    interface OnTaskClickListener {
        void onEditClick(Task task);
        void onDeleteClick(Task task);
    }

    public CursorRecyclerViewAdapter(Cursor cursor, OnTaskClickListener listener) {
        this.cursor = cursor;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_items, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        if (cursor == null || cursor.getCount() == 0) {
            holder.name.setText(R.string.instructions_heading);
            holder.description.setText(R.string.instructions);
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
        } else {
            if (!cursor.moveToPosition(position)) {
                throw new IllegalStateException("Couldn't move cursor to position " + position);
            }

            final Task task = new Task(cursor.getLong(cursor.getColumnIndex(TasksContract.Columns._ID)),
                    cursor.getString(cursor.getColumnIndex(TasksContract.Columns.TASKS_NAME)),
                    cursor.getString(cursor.getColumnIndex(TasksContract.Columns.TASKS_DESCRIPTION)),
                    cursor.getInt(cursor.getColumnIndex(TasksContract.Columns.TASKS_SORT_ORDER)));

            holder.name.setText(task.getName());
            holder.description.setText(task.getDescription());
            holder.editButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);

            View.OnClickListener buttonListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (view.getId()) {
                        case R.id.tli_edit:
                            if (listener != null) {
                                listener.onEditClick(task);
                            }
                            break;
                        case R.id.tli_delete:
                            if (listener != null) {
                                listener.onDeleteClick(task);
                            }
                            break;
                    }
                }
            };

            holder.editButton.setOnClickListener(buttonListener);
            holder.deleteButton.setOnClickListener(buttonListener);
        }
    }

    @Override
    public int getItemCount() {
        if (cursor == null || cursor.getCount() == 0) {
            return 1;
        } else {
            return cursor.getCount();
        }
    }

    Cursor swapCursor(Cursor newCursor) {
        if (newCursor == cursor) {
            return null;
        }

        final Cursor oldCursor = cursor;
        cursor = newCursor;
        if (newCursor != null) {
            notifyDataSetChanged();
        } else {
            notifyItemRangeRemoved(0, getItemCount());
        }
        return oldCursor;
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView name = null;
        TextView description = null;
        ImageButton editButton = null;
        ImageButton deleteButton = null;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            this.name = (TextView) itemView.findViewById(R.id.tli_name);
            this.description = (TextView) itemView.findViewById(R.id.tli_description);
            this.editButton = (ImageButton) itemView.findViewById(R.id.tli_edit);
            this.deleteButton = (ImageButton) itemView.findViewById(R.id.tli_delete);
        }
    }
}
