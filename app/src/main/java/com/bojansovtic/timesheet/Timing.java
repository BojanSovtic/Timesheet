package com.bojansovtic.timesheet;

import java.io.Serializable;
import java.util.Date;

class Timing implements Serializable {
    private static final long serialVersionUID = 20200903L;

    private long _id;
    private long startTime;
    private long duration;
    private Task task;

    public Timing(Task task) {
        this.task = task;
        Date currentTime = new Date();
        startTime = currentTime.getTime() / 1000;
        duration = 0;
    }

    long get_id() {
        return _id;
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
        Date currentTime = new Date();
        duration = (currentTime.getTime() / 1000) - startTime;
    }

    Task getTask() {
        return task;
    }

    void setTask(Task task) {
        this.task = task;
    }
}
