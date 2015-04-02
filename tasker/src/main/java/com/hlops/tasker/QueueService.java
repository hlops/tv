package com.hlops.tasker;

import com.hlops.tasker.task.Task;
import org.springframework.beans.factory.DisposableBean;

import java.util.concurrent.Future;

/**
 * Created by IntelliJ IDEA.
 * User: a.karnachuk
 * Date: 12/28/13
 * Time: 3:36 PM
 */
public interface QueueService extends DisposableBean {

    <T> Future<T> executeTask(Task<T> task);
}
