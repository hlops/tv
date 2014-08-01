package com.hlops.webCache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: a.karnachuk
 * Date: 2/18/14
 * Time: 2:53 PM
 */
class CachedEntry {

    private final File file;
    private final URL url;
    private final WebCacheParams params;
    private final String hash;

    public CachedEntry(File path, URL url, WebCacheParams params) {
        this.url = url;
        this.params = params;
        this.hash = calculateHash(url);
        this.file = new File(path, hash);
    }

    private String calculateHash(URL url) {
        return String.valueOf(url.toString().hashCode());
    }

    public String getHash() {
        return hash;
    }

    public InputStream read() throws IOException, URISyntaxException {
        CacheFileEntry cacheFileEntry = new CacheFileEntry(file);
        InputStream inputStream = cacheFileEntry.readFromFile(true);
        if (inputStream == null) {
            cacheFileEntry.saveToFile(url, params);
            inputStream = cacheFileEntry.readFromFile(false);
        }
        return inputStream;
    }

    public boolean remove() {
        return new CacheFileEntry(file).delete();
    }
}
