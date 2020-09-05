package com.bojansovtic.timesheet;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.security.InvalidParameterException;

public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    public static final int LOADER_ID = 0;

    private CursorRecyclerViewAdapter adapter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.task_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CursorRecyclerViewAdapter(null, (CursorRecyclerViewAdapter.OnTaskClickListener) getActivity());
        recyclerView.setAdapter(adapter);

        return view;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {TasksContract.Columns._ID, TasksContract.Columns.TASKS_NAME,
                                TasksContract.Columns.TASKS_DESCRIPTION, TasksContract.Columns.TASKS_SORT_ORDER};
        String sortOrder = TasksContract.Columns.TASKS_SORT_ORDER + "," + TasksContract.Columns.TASKS_NAME + " COLLATE NOCASE";
        switch (id) {
            case LOADER_ID:
                return new CursorLoader(getActivity(),
                        TasksContract.CONTENT_URI,
                        projection,
                        null,
                        null,
                        sortOrder);

            default:
                throw new InvalidParameterException("MainActivityFragment.onCreateLoader called with invalid " +
                        "loader id " + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        int count = adapter.getItemCount();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
