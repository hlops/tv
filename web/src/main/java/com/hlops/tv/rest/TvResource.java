package com.hlops.tv.rest;

import com.hlops.tv.core.bean.M3U;
import com.hlops.tv.core.service.TVProgramService;
import com.hlops.tv.core.service.XmltvService;
import com.hlops.tv.core.service.impl.filter.HtmlFilterFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
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
    public Response parsePlaylist(@Context final HttpServletRequest request) throws InterruptedException {
        final M3U m3U = tvProgramService.loadTV();
        StreamingOutput streamingOutput = new StreamingOutput() {
            @Override
            public void write(OutputStream outputStream) throws IOException, WebApplicationException {
                tvProgramService.print(m3U, new PrintStream(outputStream), filterFactory.createFilter(request.getParameterMap()));
            }
        };
        return Response.ok(streamingOutput).build();
    }

    @GET
    @Path("playlist.m3u")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response parsePlaylistFile(@Context final HttpServletRequest request) throws InterruptedException {
        final M3U m3U = tvProgramService.loadTV();
        StreamingOutput streamingOutput = new StreamingOutput() {
            @Override
            public void write(OutputStream outputStream) throws IOException, WebApplicationException {
                tvProgramService.print(m3U, new PrintStream(outputStream), filterFactory.createFilter(request.getParameterMap()));
            }
        };
        return Response.ok(streamingOutput).
                header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename = playlist.m3u").build();
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
    @Path("xmltv-test")
    @Produces(MediaType.TEXT_PLAIN + ";charset=utf-8")
    public Response parseXmltvTest(@Context final HttpServletRequest request) throws InterruptedException {
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
        return Response.ok(streamingOutput).build();
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
                    xmltvService.printXmltv(gzipOutputStream, filterFactory.createFilter(request.getParameterMap()));
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
