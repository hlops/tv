package com.hlops.tv.core.task;

import com.hlops.tasker.task.CacheableTask;
import com.hlops.tasker.task.impl.TaskImpl;
import com.hlops.tv.core.bean.M3U;
import com.hlops.tv.core.service.M3uService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by IntelliJ IDEA.
 * User: akarnachuk
 * Date: 7/31/14
 * Time: 4:19 PM
 */
public class DownloadM3uTask extends TaskImpl<Void> implements CacheableTask<Void> {

    private static Logger log = LogManager.getLogger(DownloadM3uTask.class);

    // 1 minute
    private static final long TIMEOUT = 60000;

    private final String url;
    private final Charset encoding;
    private final M3uService m3uService;

    public DownloadM3uTask(M3uService m3uService) {
        this.url = m3uService.getPlaylist();
        this.encoding = m3uService.getEncoding();
        this.m3uService = m3uService;
    }

    @Override
    public Void call() throws Exception {
        log.debug("Open url " + url);
        try (InputStream is = new URL(url).openStream()) {
            log.debug("reading m3u");
            M3U m3u = new M3U(is, encoding);
            log.debug("parsing channels");
            m3uService.parseChannels(m3u);
        }
        return null;
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
        if (!(o instanceof DownloadM3uTask)) return false;

        DownloadM3uTask that = (DownloadM3uTask) o;

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
