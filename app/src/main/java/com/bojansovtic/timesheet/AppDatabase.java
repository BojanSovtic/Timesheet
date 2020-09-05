package com.bojansovtic.timesheet;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class AppDatabase extends SQLiteOpenHelper {
    private static final String TAG = "AppDatabase";

    private static final String DATABASE_NAME = "Timesheet.db";
    public static final int DATABASE_VERSION = 2;

    private static AppDatabase instance = null;

    private AppDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    static AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = new AppDatabase(context);
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql;

        sql = "CREATE TABLE " + TasksContract.TABLE_NAME + "("
                + TasksContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, "
                + TasksContract.Columns.TASKS_NAME + " TEXT NOT NULL, "
                + TasksContract.Columns.TASKS_DESCRIPTION + " TEXT, "
                + TasksContract.Columns.TASKS_SORT_ORDER + " INTEGER);";
        Log.d(TAG, sql);
        db.execSQL(sql);

        addTimingsTable(db);
        addDurationsView(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                addTimingsTable(db);
            case 2:
                addDurationsView(db);
                break;
            default:
                throw new IllegalStateException("onUpgrade() with unknown newVersion: " + newVersion);
        }
    }

    private void addTimingsTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TimingsContract.TABLE_NAME + " ("
                + TimingsContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, "
                + TimingsContract.Columns.TIMINGS_START_TIME + " INTEGER, "
                + TimingsContract.Columns.TIMINGS_DURATION + " INTEGER, "
                + TimingsContract.Columns.TIMINGS_TASK_ID + " INTEGER NOT NULL);";
        Log.d(TAG, sql);
        db.execSQL(sql);

        sql = "CREATE TRIGGER Remove_Task"
                + " AFTER DELETE ON " + TasksContract.TABLE_NAME
                + " FOR EACH ROW "
                + " BEGIN "
                + " DELETE FROM " + TimingsContract.TABLE_NAME
                + " WHERE " + TimingsContract.Columns.TIMINGS_TASK_ID + " = OLD." + TasksContract.Columns._ID + ";"
                + " END;";
        Log.d(TAG, sql);
        db.execSQL(sql);
    }

    private void addDurationsView(SQLiteDatabase db) {
        String sql = "CREATE VIEW " + DurationsContract.TABLE_NAME + " AS "
                + " SELECT " + TimingsContract.TABLE_NAME + "." + TimingsContract.Columns._ID + ", "
                + TasksContract.TABLE_NAME + "." + TasksContract.Columns.TASKS_NAME + ", "
                + TasksContract.TABLE_NAME + "." + TasksContract.Columns.TASKS_DESCRIPTION + ", "
                + TimingsContract.TABLE_NAME + "." + TimingsContract.Columns.TIMINGS_START_TIME + ", "
                + " DATE(" + TimingsContract.TABLE_NAME + "." + TimingsContract.Columns.TIMINGS_START_TIME + ", 'unixepoch')"
                + " AS " + DurationsContract.Columns.DURATIONS_START_DATE + ", "
                + " SUM(" + TimingsContract.TABLE_NAME + "." + TimingsContract.Columns.TIMINGS_DURATION + ")"
                + " AS " + DurationsContract.Columns.DURATIONS_DURATION
                + " FROM " + TasksContract.TABLE_NAME + " JOIN " + TimingsContract.TABLE_NAME
                + " ON " + TasksContract.TABLE_NAME + "." + TasksContract.Columns._ID + " = "
                + TimingsContract.TABLE_NAME + "." + TimingsContract.Columns.TIMINGS_TASK_ID
                + " GROUP BY " + DurationsContract.Columns.DURATIONS_START_DATE + ", "
                + DurationsContract.Columns.DURATIONS_NAME + ";";
        Log.d(TAG, sql);
        db.execSQL(sql);
    }
}
