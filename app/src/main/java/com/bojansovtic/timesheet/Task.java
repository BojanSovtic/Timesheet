package com.bojansovtic.timesheet;

import java.io.Serializable;

class Task implements Serializable {
    public static final long serialVersionUID = 20200902L;

    private long _id;
    private final String name;
    private final String description;
    private final int sortOrder;

    public Task(long _id, String name, String description, int sortOrder) {
        this._id = _id;
        this.name = name;
        this.description = description;
        this.sortOrder = sortOrder;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    @Override
    public String toString() {
        return "Task{" +
                "_id=" + _id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", sortOrder=" + sortOrder +
                '}';
    }
}
