package com.bojansovtic.timesheet;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements CursorRecyclerViewAdapter.OnTaskClickListener,
                                                               AddEditActivityFragment.OnSaveClicked,
                                                               AppDialog.DialogEvents {

    private boolean twoPane = false;

    public static final int DIALOG_ID_DELETE = 1;
    public static final int DIAlOG_ID_CANCEL_EDIT = 2;

    private AlertDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (findViewById(R.id.task_details_container) != null) {
            twoPane = true;
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
                break;
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
    public void onEditClick(Task task) {
        taskEditRequest(task);
    }

    @Override
    public void onDeleteClick(Task task) {
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
        if (twoPane) {
            AddEditActivityFragment fragment = new AddEditActivityFragment();

            Bundle arguments = new Bundle();
            arguments.putSerializable(Task.class.getSimpleName(), task);
            fragment.setArguments(arguments);

            getSupportFragmentManager()
                    .beginTransaction().replace(R.id.task_details_container, fragment)
                    .commit();
        } else {
            Intent detailIntent = new Intent(this, AddEditActivity.class);
            if (task != null) {
                detailIntent.putExtra(Task.class.getSimpleName(), task);
                startActivity(detailIntent);
            } else {
                startActivity(detailIntent);
            }
        }
    }

    @Override
    public void onPositiveDialogResult(int dialogId, Bundle args) {
        switch (dialogId) {
            case DIALOG_ID_DELETE:
                Long taskId = args.getLong("TaskId");
                if (BuildConfig.DEBUG && taskId == 0) throw new AssertionError("Task ID is zero");
                getContentResolver().delete(TasksContract.buildTaskUri(taskId), null, null);
                break;
            case DIAlOG_ID_CANCEL_EDIT:
                break;
        }
    }

    @Override
    public void onNegativeDialogResult(int dialogId, Bundle args) {
        switch (dialogId) {
            case DIALOG_ID_DELETE:
                break;
            case DIAlOG_ID_CANCEL_EDIT:
                finish();
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
            AppDialog dialog = new AppDialog();
            Bundle args = new Bundle();
            args.putInt(AppDialog.DIALOG_ID, DIAlOG_ID_CANCEL_EDIT);
            args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.cancelEditDiag_message));
            args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.cancelEditDiag_positive_caption);
            args.putInt(AppDialog.DIALOG_NEGATIVE_RID, R.string.cancelEditDiag_negative_caption);

            dialog.setArguments(args);
            dialog.show(getSupportFragmentManager(), null);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}