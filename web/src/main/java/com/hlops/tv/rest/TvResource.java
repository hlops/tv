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
import javax.ws.rs.core.*;
import java.io.*;
import java.util.zip.GZIPOutputStream;

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
    @Path("playlist.local")
    @Produces(MediaType.TEXT_PLAIN + ";charset=utf-8")
    public InputStream readPlaylist() {
        return getClass().getResourceAsStream("/playlist.m3u8");
//        return getClass().getResourceAsStream("/playlist (1).m3u8");
    }

    @GET
    @Path("xmltv")
    @Produces(MediaType.APPLICATION_XML)
    public Response parseXmltv(@Context final HttpServletRequest request) throws InterruptedException {
        StreamingOutput streamingOutput = new StreamingOutput() {
            @Override
            public void write(OutputStream outputStream) throws IOException, WebApplicationException {
                try {
                    xmltvService.printXmltv(outputStream, filterFactory.createFilter(request.getParameterMap()));
                } catch (InterruptedException e) {
                    // nothing to do
                }
            }
        };
        return Response.ok(streamingOutput).
                header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename = xmltv.xml").build();
    }

    @GET
    @Path("xmltv.gz")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response parseXmltvGz(@Context final HttpServletRequest request) throws InterruptedException {
        StreamingOutput streamingOutput = new StreamingOutput() {
            @Override
            public void write(OutputStream outputStream) throws IOException, WebApplicationException {
                GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream, true);
                try {
                    xmltvService.printXmltv(outputStream, filterFactory.createFilter(request.getParameterMap()));
                } catch (InterruptedException e) {
                    // nothing to do
                } finally {
                    gzipOutputStream.close();
                }
            }
        };
        return Response.ok(streamingOutput).
                header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename = xmltv.xml.gz").build();
    }

}
