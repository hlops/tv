package com.hlops.tasker.impl;

import com.hlops.tasker.task.CacheableTask;
import com.hlops.tasker.task.Task;

import java.util.concurrent.FutureTask;

/**
 * Created by IntelliJ IDEA.
 * User: a.karnachuk
 * Date: 12/30/13
 * Time: 1:41 PM
 */
class PriorityFutureTask<T> extends FutureTask<T> implements Comparable {

    private final Task<T> task;
    private final long expirationTime;

    public PriorityFutureTask(Task<T> task) {
        super(task);
        this.task = task;
        if (task instanceof CacheableTask<?>) {
            expirationTime = System.currentTimeMillis() + ((CacheableTask) task).getAliveTime();
        } else {
            expirationTime = -1;
        }
    }

    Task<T> getTask() {
        return task;
    }

    public int compareTo(Object o) {
        return task.compareTo(((PriorityFutureTask) o).getTask());
    }

    public boolean expired() {
        return expirationTime > 0 && System.currentTimeMillis() > expirationTime;
    }
}
