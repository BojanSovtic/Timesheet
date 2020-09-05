package com.bojansovtic.timesheet;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bojansovtic.timesheet.debug.TestData;

public class MainActivity extends AppCompatActivity implements CursorRecyclerViewAdapter.OnTaskClickListener,
                                                               AddEditActivityFragment.OnSaveClicked,
                                                               AppDialog.DialogEvents {
    private static final String TAG = "MainActivity";

    private boolean twoPane = false;

    public static final int DIALOG_ID_DELETE = 1;
    public static final int DIAlOG_ID_CANCEL_EDIT = 2;
    private static final int DIALOG_ID_CANCEL_EDIT_UP = 3;

    private AlertDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        twoPane = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
        Log.d(TAG, "onCreate: twoPane is " + twoPane);

        FragmentManager fragmentManager = getSupportFragmentManager();
        // If the AddEditActivity fragment exists, we're editing
        Boolean editing = fragmentManager.findFragmentById(R.id.task_details_container) != null;
        Log.d(TAG, "onCreate: editing is " + editing);

        // We need references to the containers, so we can show or hide them as necessary
        // No need to cast them, as we're only calling a method that's available for all views
        View addEditLayout = findViewById(R.id.task_details_container);
        View mainFragment = findViewById(R.id.fragment);

        if (twoPane) {
            Log.d(TAG, "onCreate: twoPane mode");
            mainFragment.setVisibility(View.VISIBLE);
            addEditLayout.setVisibility(View.VISIBLE);
        } else if (editing) {
            Log.d(TAG, "onCreate: single pane, editing");
            // Hide the left hand fragment, to make room for editing
            mainFragment.setVisibility(View.GONE);
        } else {
            Log.d(TAG, "onCreate: single pane, not editing");
            // Show left hand fragment
            mainFragment.setVisibility(View.VISIBLE);
            // Hide the editing frame
            addEditLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSaveClicked() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.task_details_container);
        if (fragment != null) {
            fragmentManager.beginTransaction()
                    .remove(fragment)
                    .commit();
        }

        View addEditLayout = findViewById(R.id.task_details_container);
        View mainFragment = findViewById(R.id.fragment);

        if (!twoPane) {
            // We've just removed the editing fragment, so hide the frame
            addEditLayout.setVisibility(View.GONE);

            // And make sure the MainActivityFragment is visible
            mainFragment.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        if (BuildConfig.DEBUG) {
            MenuItem generate = menu.findItem(R.id.menumain_generate);
            generate.setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menumain_addTask:
                taskEditRequest(null);
                break;
            case R.id.menumain_showDurations:
                break;
            case R.id.menumain_settings:
                break;
            case R.id.menumain_showAbout:
                showAboutDialog();
                break;
            case R.id.menumain_generate:
                TestData.generateTestData(getContentResolver());
                break;
            case android.R.id.home:
                Log.d(TAG, "onOptionsItemSelected: home button pressed");
                AddEditActivityFragment fragment = (AddEditActivityFragment)
                        getSupportFragmentManager().findFragmentById(R.id.task_details_container);
                if (fragment.canClose()) {
                    return super.onOptionsItemSelected(item);
                } else {
                    showConfirmationDialog(DIALOG_ID_CANCEL_EDIT_UP);
                    return true;  // indicate we are handling this
                }
        }

        return super.onOptionsItemSelected(item);
    }

    public void showAboutDialog() {
        View messageView = getLayoutInflater().inflate(R.layout.about, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.mipmap.ic_launcher);

        builder.setView(messageView);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);

        TextView tv = (TextView) messageView.findViewById(R.id.about_version);
        tv.setText("v" + BuildConfig.VERSION_NAME);

        TextView about_url = (TextView) messageView.findViewById(R.id.about_url);
        if (about_url != null) {
            about_url.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    String s = ((TextView) view).getText().toString();
                    intent.setData(Uri.parse(s));
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(MainActivity.this, "No browser application found",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        dialog.show();
    }

    @Override
    public void onEditClick(@NonNull Task task) {
        taskEditRequest(task);
    }

    @Override
    public void onDeleteClick(@NonNull Task task) {
        AppDialog dialog = new AppDialog();
        Bundle args = new Bundle();
        args.putInt(AppDialog.DIALOG_ID, DIALOG_ID_DELETE);
        args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.deldiag_message, task.get_id(), task.getName()));
        args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.deldiag_positive_caption);

        args.putLong("TaskId", task.get_id());

        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), null);
    }

    private void taskEditRequest(Task task) {
        AddEditActivityFragment fragment = new AddEditActivityFragment();

        Bundle arguments = new Bundle();
        arguments.putSerializable(Task.class.getSimpleName(), task);
        fragment.setArguments(arguments);

        Log.d(TAG, "taskEditRequest: twoPaneMode");
        getSupportFragmentManager()
                .beginTransaction().replace(R.id.task_details_container, fragment)
                .commit();
        if (!twoPane) {
            Log.d(TAG, "taskEditRequest: in single-pane mode (phone)");
            // Hide the left hand fragment and show the right hand frame
            View mainFragment = findViewById(R.id.fragment);
            View addEditLayout = findViewById(R.id.task_details_container);
            mainFragment.setVisibility(View.GONE);
            addEditLayout.setVisibility(View.VISIBLE);
        }
        Log.d(TAG, "taskEditRequest: Exiting");
    }

    @Override
    public void onPositiveDialogResult(int dialogId, Bundle args) {
        switch (dialogId) {
            case DIALOG_ID_DELETE:
                Long taskId = args.getLong("TaskId");
                getContentResolver().delete(TasksContract.buildTaskUri(taskId), null, null);
                break;
            case DIAlOG_ID_CANCEL_EDIT:
            case DIALOG_ID_CANCEL_EDIT_UP:
                break;
        }
    }

    @Override
    public void onNegativeDialogResult(int dialogId, Bundle args) {
        switch (dialogId) {
            case DIALOG_ID_DELETE:
                break;
            case DIAlOG_ID_CANCEL_EDIT:
            case DIALOG_ID_CANCEL_EDIT_UP:
                // If we're editing, remove the fragment. Otherwise, close the app
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment fragment = fragmentManager.findFragmentById(R.id.task_details_container);
                if (fragment != null) {
                    // We were editing
                    getSupportFragmentManager().beginTransaction()
                            .remove(fragment)
                            .commit();

                    if (twoPane) {
                        // In Landscape, quit only if the back button was used
                        if (dialogId == DIAlOG_ID_CANCEL_EDIT) {
                            finish();
                        }
                    } else {
                        // Hide the edit container in single pane mode
                        // and make sure the left-hand container is visible
                        View addEditLayout = findViewById(R.id.task_details_container);
                        View mainFragment = findViewById(R.id.fragment);
                        // We've just removed the editing fragment, so hide the frame
                        addEditLayout.setVisibility(View.GONE);

                        // and make sure the MainActivityFragment is visible
                        mainFragment.setVisibility(View.VISIBLE);
                    }
                } else {
                    // Not editing, so quit regardless of orientation
                    finish();
                }
                break;
        }
    }

    @Override
    public void onDialogCancelled(int dialogId) {
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        AddEditActivityFragment fragment = (AddEditActivityFragment) fragmentManager
                .findFragmentById(R.id.task_details_container);
        if (fragment == null || fragment.canClose()) {
            super.onBackPressed();
        } else {
            // show dialogue to get confirmation to quit editing
            showConfirmationDialog(DIAlOG_ID_CANCEL_EDIT);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void showConfirmationDialog(int dialogId) {
        AppDialog dialog = new AppDialog();
        Bundle args = new Bundle();
        args.putInt(AppDialog.DIALOG_ID, dialogId);
        args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.cancelEditDiag_message));
        args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.cancelEditDiag_positive_caption);
        args.putInt(AppDialog.DIALOG_NEGATIVE_RID, R.string.cancelEditDiag_negative_caption);

        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), null);
    }

    @Override
    public void onTaskLongClick(@NonNull Task task) {
        // Required to satisfy the interface
    }
}