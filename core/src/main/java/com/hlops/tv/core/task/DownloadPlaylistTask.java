package com.hlops.tv.core.task;

import com.hlops.tasker.task.CacheableTask;
import com.hlops.tasker.task.impl.TaskImpl;
import com.hlops.tv.core.bean.M3U;
import com.sun.istack.internal.NotNull;

import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by IntelliJ IDEA.
 * User: akarnachuk
 * Date: 7/31/14
 * Time: 4:19 PM
 */
public class DownloadPlaylistTask extends TaskImpl<M3U> implements CacheableTask<M3U> {

    // 1 minute
    private static final long TIMEOUT = 60000;

    private final String url;
    private final Charset encoding;

    public DownloadPlaylistTask(@NotNull String url, @NotNull Charset encoding) {
        this.url = url;
        this.encoding = encoding;
    }

    @Override
    public M3U call() throws Exception {
        return new M3U(new URL(url).openStream(), encoding);
    }

    @Override
    public Object getId() {
        return hashCode();
    }

    @Override
    public long getAliveTime() {
        return TIMEOUT;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DownloadPlaylistTask)) return false;

        DownloadPlaylistTask that = (DownloadPlaylistTask) o;

        if (!encoding.equals(that.encoding)) return false;
        if (!url.equals(that.url)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = url.hashCode();
        result = 31 * result + encoding.hashCode();
        return result;
    }
}
