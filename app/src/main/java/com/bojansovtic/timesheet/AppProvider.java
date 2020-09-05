package com.bojansovtic.timesheet;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AppProvider extends ContentProvider {
    private AppDatabase openHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    static final String CONTENT_AUTHORITY = "com.bojansovtic.timesheet.provider";
    public static final Uri CONTENT_AUTHORITY_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final int TASKS = 100;
    private static final int TASKS_ID = 101;

    private static final int TIMINGS = 200;
    private static final int TIMINGS_ID = 201;

    // TODO
    private static final int TASKS_TIMINGS = 300;
    private static final int TASKS_TIMINGS_ID = 301;


    private static final int TASK_DURATIONS = 400;
    private static final int TASK_DURATIONS_ID = 401;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(CONTENT_AUTHORITY, TasksContract.TABLE_NAME, TASKS);
        matcher.addURI(CONTENT_AUTHORITY, TasksContract.TABLE_NAME + "/#", TASKS_ID);

        matcher.addURI(CONTENT_AUTHORITY, TimingsContract.TABLE_NAME, TIMINGS);
        matcher.addURI(CONTENT_AUTHORITY, TimingsContract.TABLE_NAME + "/#", TIMINGS_ID);

        matcher.addURI(CONTENT_AUTHORITY, DurationsContract.TABLE_NAME, TASK_DURATIONS);
        matcher.addURI(CONTENT_AUTHORITY, DurationsContract.TABLE_NAME + "/#", TASK_DURATIONS_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        openHelper = AppDatabase.getInstance(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final int match = sUriMatcher.match(uri);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (match) {
            case TASKS:
                queryBuilder.setTables(TasksContract.TABLE_NAME);
                break;

            case TASKS_ID:
                queryBuilder.setTables(TasksContract.TABLE_NAME);
                long taskId = TasksContract.getTaskId(uri);
                queryBuilder.appendWhere(TasksContract.Columns._ID + " = " + taskId);
                break;

            case TIMINGS:
                queryBuilder.setTables(TimingsContract.TABLE_NAME);
                break;

            case TIMINGS_ID:
                queryBuilder.setTables(TimingsContract.TABLE_NAME);
                long timingId = TimingsContract.getTimingId(uri);
                queryBuilder.appendWhere(TimingsContract.Columns._ID + " = " + timingId);
                break;

            case TASK_DURATIONS:
                queryBuilder.setTables(DurationsContract.TABLE_NAME);
                break;

            case TASK_DURATIONS_ID:
                queryBuilder.setTables(DurationsContract.TABLE_NAME);
                long durationId = DurationsContract.getDurationId(uri);
                queryBuilder.appendWhere(DurationsContract.Columns._ID + " = " + durationId);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = openHelper.getReadableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TASKS:
                return TasksContract.CONTENT_TYPE;

            case TASKS_ID:
                return TasksContract.CONTENT_ITEM_TYPE;

            case TIMINGS:
                return TimingsContract.CONTENT_TYPE;

            case TIMINGS_ID:
                return TimingsContract.CONTENT_ITEM_TYPE;

            case TASK_DURATIONS:
                return DurationsContract.CONTENT_TYPE;

            case TASK_DURATIONS_ID:
                return DurationsContract.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);

        final SQLiteDatabase db;

        Uri returnUri;
        long recordId;

        switch (match) {
            case TASKS:
                db = openHelper.getWritableDatabase();
                recordId = db.insert(TasksContract.TABLE_NAME, null, contentValues);
                if (recordId >= 0) {
                    returnUri = TasksContract.buildTaskUri(recordId);
                } else {
                    throw new android.database.SQLException("Failed to insert into " + uri.toString());
                }
                break;

            case TIMINGS:
                db = openHelper.getWritableDatabase();
                recordId = db.insert(TimingsContract.TABLE_NAME, null, contentValues);
                if (recordId >= 0) {
                    returnUri = TimingsContract.buildTimingUri(recordId);
                } else {
                    throw new android.database.SQLException("Failed to insert into " + uri.toString());
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (recordId >= 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);

        final SQLiteDatabase db;
        int count;

        String selectionCriteria;

        switch (match) {
            case TASKS:
                db = openHelper.getWritableDatabase();
                count = db.delete(TasksContract.TABLE_NAME, selection, selectionArgs);
                break;

            case TASKS_ID:
                db = openHelper.getWritableDatabase();
                long taskId = TasksContract.getTaskId(uri);
                selectionCriteria = TasksContract.Columns._ID + " = " + taskId;

                if (selection != null && selection.length() > 0) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.delete(TasksContract.TABLE_NAME, selectionCriteria, selectionArgs);
                break;

            case TIMINGS:
                db = openHelper.getWritableDatabase();
                count = db.delete(TimingsContract.TABLE_NAME, selection, selectionArgs);
                break;

            case TIMINGS_ID:
                db = openHelper.getWritableDatabase();
                long timingsId = TimingsContract.getTimingId(uri);
                selectionCriteria = TimingsContract.Columns._ID + " = " + timingsId;

                if (selection != null && selection.length() > 0) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.delete(TimingsContract.TABLE_NAME, selectionCriteria, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);

        final SQLiteDatabase db;
        int count;

        String selectionCriteria;

        switch (match) {
            case TASKS:
                db = openHelper.getWritableDatabase();
                count = db.update(TasksContract.TABLE_NAME, contentValues, selection, selectionArgs);
                break;

            case TASKS_ID:
                db = openHelper.getWritableDatabase();
                long taskId = TasksContract.getTaskId(uri);
                selectionCriteria = TasksContract.Columns._ID + " = " + taskId;

                if (selection != null && selection.length() > 0) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.update(TasksContract.TABLE_NAME, contentValues, selectionCriteria, selectionArgs);
                break;

            case TIMINGS:
                db = openHelper.getWritableDatabase();
                count = db.update(TimingsContract.TABLE_NAME, contentValues, selection, selectionArgs);
                break;

            case TIMINGS_ID:
                db = openHelper.getWritableDatabase();
                long timingsId = TimingsContract.getTimingId(uri);
                selectionCriteria = TimingsContract.Columns._ID + " = " + timingsId;

                if (selection != null && selection.length() > 0) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.update(TimingsContract.TABLE_NAME, contentValues, selectionCriteria, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return count;
    }
}
