package com.hlops.tv.core.task;

import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.FileCopyUtils;

import com.hlops.tasker.task.CacheableTask;
import com.hlops.tasker.task.impl.TaskImpl;
import com.hlops.tv.core.service.XmltvService;

/**
 * Created by IntelliJ IDEA.
 * User: akarnachuk
 * Date: 7/31/14
 * Time: 4:19 PM
 */
public class DownloadXmltvTask extends TaskImpl<Void> implements CacheableTask<Void> {

    private static Logger log = LogManager.getLogger(DownloadXmltvTask.class);

    // 1 hour
    private static final long TIMEOUT = 60 * 60 * 1000;

    private static final DateFormat HTTP_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");

    static {
        HTTP_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    private File file;
    private final String url;
    private XmltvService xmltvService;

    public DownloadXmltvTask(XmltvService xmltvService) {
        this.xmltvService = xmltvService;
        this.file = xmltvService.getFile();
        this.url = xmltvService.getUrl();
    }

    @Override
    public Void call() throws Exception {
        log.info("file " + file.getAbsolutePath());
        if (file.exists() && !(file.canRead()) && file.canWrite()) {
            throw new IllegalArgumentException("File " + file.getAbsolutePath() + " is protected");
        }
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(30000);

        connection.setRequestProperty("If-Modified-Since", HTTP_DATE_FORMAT.format(new Date(file.lastModified())));
        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_NOT_MODIFIED) {
            log.info("not modified");
        } else if (responseCode == HttpURLConnection.HTTP_OK) {
            log.info("200 ok");
            if (file.exists()) {
                FileCopyUtils.copy(file, new File(file.getParentFile(), file.getName() + "~"));
            }
            FileCopyUtils.copy(connection.getInputStream(), new FileOutputStream(file));
            //noinspection ResultOfMethodCallIgnored
            file.setLastModified(connection.getLastModified());
            xmltvService.setProgramBindingDirty(true);
        } else {
            log.error("Unexpected responseCode=" + responseCode);
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
