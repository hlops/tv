package com.hlops.tv.rest;

import com.hlops.tv.core.bean.M3U;
import com.hlops.tv.core.service.TVProgramService;
import com.hlops.tv.core.service.XmltvService;
import com.hlops.tv.core.service.impl.HtmlFilterFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;

/**
 * Created by tom on 3/27/15.
 */
@Path("/")
@Component
public class TvResource {

    @Autowired
    TVProgramService tvProgramService;

    @Autowired
    XmltvService xmltvService;

    @Autowired
    HtmlFilterFactory filterFactory;

    @GET
    @Path("playlist")
    @Produces(MediaType.TEXT_PLAIN + ";charset=utf-8")
    public String parsePlaylist(@Context HttpServletRequest request) throws InterruptedException {
        M3U m3U = tvProgramService.loadTV();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        tvProgramService.print(m3U, new PrintStream(out), filterFactory.createFilter(request.getParameterMap()));
        return out.toString();
    }

    @GET
    @Path("playlist.m3u")
    @Produces(MediaType.TEXT_PLAIN + ";charset=utf-8")
    public InputStream readPlaylist() {
        return getClass().getResourceAsStream("/playlist.m3u8");
//        return getClass().getResourceAsStream("/playlist (1).m3u8");
    }

    @GET
    @Path("xmltv")
    @Produces(MediaType.TEXT_PLAIN + ";charset=utf-8")
    public StreamingOutput parseXmltv(@Context final HttpServletRequest request) throws InterruptedException {
        return new StreamingOutput() {
            @Override
            public void write(OutputStream outputStream) throws IOException, WebApplicationException {
                try {
                    xmltvService.getXmltv(outputStream, filterFactory.createFilter(request.getParameterMap()));
                } catch (InterruptedException e) {
                    // nothing to do
                }
            }
        };
    }

}
