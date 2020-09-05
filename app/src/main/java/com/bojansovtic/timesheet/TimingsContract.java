package com.bojansovtic.timesheet;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class TimingsContract {

    static final String TABLE_NAME = "timings";

    public static class Columns {
        public static final String _ID = BaseColumns._ID;
        public static final String TIMINGS_START_TIME = "start_time";
        public static final String TIMINGS_DURATION = "duration";
        public static final String TIMINGS_TASK_ID = "task_id";

        private Columns() {
        }
    }

    public static final Uri CONTENT_URI = Uri.withAppendedPath(AppProvider.CONTENT_AUTHORITY_URI, TABLE_NAME);

    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AppProvider.CONTENT_AUTHORITY
            + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + AppProvider.CONTENT_AUTHORITY
            + "." + TABLE_NAME;

    public static Uri buildTimingUri(long TIMINGId) {
        return ContentUris.withAppendedId(CONTENT_URI, TIMINGId);
    }

    public static long getTimingId(Uri uri) {
        return ContentUris.parseId(uri);
    }
}
