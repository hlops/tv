package com.hlops.webCache;

/**
 * Created by IntelliJ IDEA.
 * User: a.karnachuk
 * Date: 2/18/14
 * Time: 2:29 PM
 */
public class WebCacheParams {

    private int connectionTimeout = 1000;
    private int readTimeout = 1000;
    private long liveTime = 3600000 * 24;

    public WebCacheParams() {
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public long getLiveTime() {
        return liveTime;
    }

    public void setLiveTime(long liveTime) {
        this.liveTime = liveTime;
    }
}
