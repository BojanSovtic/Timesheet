package com.bojansovtic.timesheet;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class TasksContract {

    static final String TABLE_NAME = "tasks";

    public static class Columns {
        public static final String _ID = BaseColumns._ID;
        public static final String TASKS_NAME = "name";
        public static final String TASKS_DESCRIPTION = "description";
        public static final String TASKS_SORT_ORDER = "sort_order";

        private Columns() {
        }
    }

    public static final Uri CONTENT_URI = Uri.withAppendedPath(AppProvider.CONTENT_AUTHORITY_URI, TABLE_NAME);

    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AppProvider.CONTENT_AUTHORITY
            + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + AppProvider.CONTENT_AUTHORITY
            + "." + TABLE_NAME;

    static Uri buildTaskUri(long taskId) {
        return ContentUris.withAppendedId(CONTENT_URI, taskId);
    }

    static long getTaskId(Uri uri) {
        return ContentUris.parseId(uri);
    }
}
