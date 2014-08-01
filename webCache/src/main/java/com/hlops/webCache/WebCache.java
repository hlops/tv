package com.hlops.webCache;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: a.karnachuk
 * Date: 2/18/14
 * Time: 2:27 PM
 */
public class WebCache {

    private final File path;

    public WebCache(@NotNull String path) {
        this(new File(path));
    }
    public WebCache(@NotNull File path) {
        if (!path.exists()) {
            throw new IllegalArgumentException("path not exists: " + path.getAbsolutePath());
        }
        this.path = path;
    }

    public InputStream load(URL url, WebCacheParams params) throws IOException, URISyntaxException {
        CachedEntry cachedEntry = new CachedEntry(path, url, params);
        return cachedEntry.read();
    }

    public void remove(URL url, WebCacheParams params) {
        new CachedEntry(path, url, params).remove();
    }

}
