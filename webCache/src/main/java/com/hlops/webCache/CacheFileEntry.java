package com.hlops.webCache;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: a.karnachuk
 * Date: 2/18/14
 * Time: 3:05 PM
 */
public class CacheFileEntry {

    private static final DateFormat DATE_FORMAT = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT);

    private File file;
    private ZipInputStream zip;

    public CacheFileEntry(File file) {
        this.file = new File(file.getParent(), file.getName() + ".zip");
    }

    public boolean delete() {
        return file.exists() && file.delete();
    }

    public InputStream readFromFile(boolean checkExpired) throws IOException {
        if (zip == null) {
            try {
                if (file.exists()) {
                    zip = new ZipInputStream(new FileInputStream(file));
                    // read url
                    zip.getNextEntry();
                    // read expires
                    zip.getNextEntry();
                    BufferedReader br = new BufferedReader(new InputStreamReader(zip, "UTF-8"));
                    Date expires = DATE_FORMAT.parse(br.readLine());
                    if (checkExpired && expires.before(new Date())) {
                        throw new ExpiredException();
                    }
                    // read html source
                    zip.getNextEntry();
                }
            } catch (ExpiredException e) {
                zip = null;
                delete();
            } catch (ParseException e) {
                zip = null;
                delete();
            }
        }
        return zip;
    }

    public void saveToFile(URL url, WebCacheParams params) throws IOException, URISyntaxException {
        ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(file));

        // url
        ZipEntry urlEntry = new ZipEntry("url");
        zip.putNextEntry(urlEntry);
        zip.write(url.toURI().toString().getBytes("utf-8"));

        // expires
        urlEntry = new ZipEntry("expires");
        zip.putNextEntry(urlEntry);
        zip.write(DATE_FORMAT.format(new Date(System.currentTimeMillis() + params.getLiveTime())).getBytes("utf-8"));

        // html source
        ZipEntry data = new ZipEntry("html");
        zip.putNextEntry(data);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(params.getConnectionTimeout());
        connection.setReadTimeout(params.getReadTimeout());
        IOUtils.copy(connection.getInputStream(), zip);

        // response code
        ZipEntry responseCode = new ZipEntry("responseCode");
        zip.putNextEntry(responseCode);
        zip.write(String.valueOf(connection.getResponseCode()).getBytes("utf-8"));

        // content type
        ZipEntry contentType = new ZipEntry("contentType");
        zip.putNextEntry(contentType);
        zip.write(String.valueOf(connection.getContentType()).getBytes("utf-8"));

        zip.close();
    }
}
