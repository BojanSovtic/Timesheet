package com.bojansovtic.timesheet;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class AppDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Timesheet.db";
    public static final int DATABASE_VERSION = 1;

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

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                // TODO upgrade logic from version 1
                break;
            default:
                throw new IllegalStateException("onUpgrade() with unknown newVersion: " + newVersion);
        }
    }
}
