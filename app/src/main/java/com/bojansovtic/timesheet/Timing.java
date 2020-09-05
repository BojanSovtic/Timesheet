package com.bojansovtic.timesheet;

import android.util.Log;

import java.io.Serializable;
import java.util.Date;

/**
 * Simple timing object.
 * Sets its start time when created, and calculates how long since creation,
 * when setDuration is called.
 */

class Timing implements Serializable {
    private static final String TAG = "Timing";
    private static final long serialVersionUID = 20200903L;

    private long _id;
    private long startTime;
    private long duration;
    private Task task;

    public Timing(Task task) {
        this.task = task;
        // Initialise the start time to now and the duration to zero for a new object
        Date currentTime = new Date();
        startTime = currentTime.getTime() / 1000;  // We are only tracking while seconds, not milliseconds
        duration = 0;
    }

    long get_id() {
        return _id;
    }

    void set_id(long _id) {
        this._id = _id;
    }

    long getStartTime() {
        return startTime;
    }

    void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    long getDuration() {
        return duration;
    }

    void setDuration() {
        // Calculate the duration from startTime to dateTime
        Date currentTime = new Date();
        duration = (currentTime.getTime() / 1000) - startTime;  // Working in seconds, not milliseconds
        Log.d(TAG, task.get_id() + " - Start time: " + startTime + " | Duration: " + duration);
    }

    Task getTask() {
        return task;
    }

    void setTask(Task task) {
        this.task = task;
    }
}
