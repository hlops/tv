package com.hlops.tv.core.task;

import com.hlops.tasker.task.CacheableTask;
import com.hlops.tasker.task.impl.TaskImpl;
import com.sun.istack.internal.NotNull;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by IntelliJ IDEA.
 * User: akarnachuk
 * Date: 7/31/14
 * Time: 4:19 PM
 */
public class DownloadXmltvTask extends TaskImpl<Void> implements CacheableTask<Void> {

    // 1 hour
    private static final long TIMEOUT = 60 * 60 * 1000;

    private static final DateFormat HTTP_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");

    static {
        HTTP_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    private File file;
    private final String url;

    public DownloadXmltvTask(@NotNull File file, @NotNull String url) {
        this.file = file;
        this.url = url;
    }

    @Override
    public Void call() throws Exception {
        if (file.exists() && !(file.canRead()) && file.canWrite()) {
            throw new IllegalArgumentException("File " + file.getAbsolutePath() + " is protected");
        }
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(30000);

        connection.setRequestProperty("If-Modified-Since", HTTP_DATE_FORMAT.format(new Date(file.lastModified())));
        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_NOT_MODIFIED) {
            // do nothing
        } else if (responseCode == HttpURLConnection.HTTP_OK) {
            FileCopyUtils.copy(connection.getInputStream(), new FileOutputStream(file));
            //noinspection ResultOfMethodCallIgnored
            file.setLastModified(connection.getLastModified());
        } else {
            throw new RuntimeException("Resource not found: " + url);
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
        if (o == null || getClass() != o.getClass()) return false;

        DownloadXmltvTask that = (DownloadXmltvTask) o;

        if (!file.equals(that.file)) return false;
        if (!url.equals(that.url)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = file.hashCode();
        result = 31 * result + url.hashCode();
        return result;
    }
}
