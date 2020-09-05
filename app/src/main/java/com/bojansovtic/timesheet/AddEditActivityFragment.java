package com.bojansovtic.timesheet;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class AddEditActivityFragment extends Fragment {

    private enum FragmentEditMode { EDIT, ADD }
    private FragmentEditMode mode;

    private EditText nameTextView;
    private EditText descriptionTextView;
    private EditText sortOrderTextView;
    private OnSaveClicked saveListener = null;

    interface OnSaveClicked {
        void onSaveClicked();
    }

    public boolean canClose() {
        return false;  // TODO
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        Activity activity = getActivity();
        if (!(activity instanceof OnSaveClicked)) {
            throw new ClassCastException(activity.getClass().getSimpleName()
                    + " must implement AddEditActivityFragment.OnSaveClicked interface");
        }
        saveListener = (OnSaveClicked) activity;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        saveListener = null;
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_edit, container, false);

        nameTextView = view.findViewById(R.id.addedit_name);
        descriptionTextView = view.findViewById(R.id.addedit_description);
        sortOrderTextView = view.findViewById(R.id.addedit_sortorder);
        Button saveButton = view.findViewById(R.id.addedit_save);

        Bundle arguments = getArguments();

        final Task task;
        if (arguments != null) {

            task = (Task) arguments.getSerializable(Task.class.getSimpleName());
            if (task != null) {
                nameTextView.setText(task.getName());
                descriptionTextView.setText(task.getDescription());
                sortOrderTextView.setText(Integer.toString(task.getSortOrder()));
                mode = FragmentEditMode.EDIT;
            } else {
                mode = FragmentEditMode.ADD;
            }
        } else {
            task = null;
            mode = FragmentEditMode.ADD;
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int sortOrder;
                if (sortOrderTextView.length() > 0) {
                    sortOrder = Integer.parseInt(sortOrderTextView.getText().toString());
                } else {
                    sortOrder = 0;
                }

                ContentResolver contentResolver = getActivity().getContentResolver();
                ContentValues values = new ContentValues();

                switch (mode) {
                    case EDIT:
                        if (task == null) {
                            // remove lint warnings, will never execute
                            break;
                        }
                        if (!nameTextView.getText().toString().equals(task.getName())) {
                            values.put(TasksContract.Columns.TASKS_NAME, nameTextView.getText().toString());
                        }
                        if (!descriptionTextView.getText().toString().equals(task.getDescription())) {
                            values.put(TasksContract.Columns.TASKS_DESCRIPTION, descriptionTextView.getText().toString());
                        }
                        if (sortOrder != task.getSortOrder()) {
                            values.put(TasksContract.Columns.TASKS_SORT_ORDER, sortOrder);
                        }
                        if (values.size() != 0) {
                            contentResolver.update(TasksContract.buildTaskUri(task.get_id()), values,
                                    null, null);
                        }
                        break;

                    case ADD:
                        if (nameTextView.length() > 0) {
                            values.put(TasksContract.Columns.TASKS_NAME, nameTextView.getText().toString());
                            values.put(TasksContract.Columns.TASKS_DESCRIPTION, descriptionTextView.getText().toString());
                            values.put(TasksContract.Columns.TASKS_SORT_ORDER, sortOrder);
                            contentResolver.insert(TasksContract.CONTENT_URI, values);
                        }
                        break;
                }

                if (saveListener != null) {
                    saveListener.onSaveClicked();
                }
            }
        });

        return view;
    }
}