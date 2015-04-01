package com.hlops.tv.rest;

import com.hlops.tv.core.bean.M3U;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

/**
 * Created by tom on 3/27/15.
 */
@Path("/")
public class TvResource {

    @GET
    @Path("hi")
    @Produces(MediaType.TEXT_PLAIN + ";charset=utf-8")
    public String hi() {
        try {
            URL url = new URL("http://www.cn.ru/data/tv/playlist.m3u");
            URLConnection urlConnection = url.openConnection();
            M3U m3U = new M3U(urlConnection.getInputStream(), Charset.forName("UTF-8"));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            m3U.save(new PrintStream(out));
            return out.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ups...";
    }

}
