package com.hlops.tv.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import com.hlops.tv.core.bean.db.DbChannel;
import com.hlops.tv.core.bean.db.DbGuide;
import com.hlops.tv.core.bean.db.DbTvItem;
import com.hlops.tv.core.service.Filter;
import com.hlops.tv.core.service.TVProgramService;
import com.hlops.tv.core.service.XmltvService;
import com.hlops.tv.core.service.impl.filter.HtmlFilterFactory;
import com.hlops.tv.model.ChannelVO;
import com.hlops.tv.model.TvItemVO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

/**
 * Created by tom on 3/27/15.
 */
@Path("/")
@Component
public class TvResource {

    private static Logger log = LogManager.getLogger(TvResource.class);

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
        //final M3U m3u = tvProgramService.loadChannels();
        StreamingOutput streamingOutput = outputStream -> {
            //tvProgramService.print(m3u, new PrintStream(outputStream), filterFactory.createFilter(request.getParameterMap()));
        };
        return Response.ok(streamingOutput).build();
    }

    @GET
    @Path("playlist.m3u")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response parsePlaylistFile(@Context final HttpServletRequest request) throws InterruptedException {
        //final M3U m3u = tvProgramService.loadChannels();
        StreamingOutput streamingOutput = outputStream -> {
            //tvProgramService.print(m3u, new PrintStream(outputStream), filterFactory.createFilter(request.getParameterMap()));
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
        StreamingOutput streamingOutput = outputStream -> {
            try {
                xmltvService.printXmltv(outputStream, filterFactory.createFilter(request.getParameterMap()));
            } catch (InterruptedException e) {
                // nothing to do
            }
        };
        return Response.ok(streamingOutput).
                header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename = xmltv.xml").build();
    }

    @GET
    @Path("xmltv-test")
    @Produces(MediaType.TEXT_PLAIN + ";charset=utf-8")
    public Response parseXmltvTest(@Context final HttpServletRequest request) throws InterruptedException {
        StreamingOutput streamingOutput = outputStream -> {
            try {
                xmltvService.printXmltv(outputStream, filterFactory.createFilter(request.getParameterMap()));
            } catch (InterruptedException e) {
                // nothing to do
            }
        };
        return Response.ok(streamingOutput).build();
    }

    @GET
    @Path("xmltv.gz")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response parseXmltvGz(@Context final HttpServletRequest request) throws InterruptedException {
        StreamingOutput streamingOutput = outputStream -> {
            try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream, true)) {
                xmltvService.printXmltv(gzipOutputStream, filterFactory.createFilter(request.getParameterMap()));
            } catch (InterruptedException e) {
                // nothing to do
            }
        };
        return Response.ok(streamingOutput).
                header("Content-Encoding", "gzip").
                header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename = xmltv.xml.gz").build();
    }


    private void printJson(OutputStream out, final Filter filter, boolean beautify) throws InterruptedException, IOException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        try (JsonWriter jsonWriter = new JsonWriter(new BufferedWriter(new OutputStreamWriter(out, "UTF-8")))) {

            if (beautify) {
                jsonWriter.setIndent("\t");
            }
            jsonWriter.beginObject();

            jsonWriter.name("channels").beginArray();
            for (DbChannel dbChannel : tvProgramService.getChannels(filter)) {
                ChannelVO channelVO = new ChannelVO(dbChannel);
                List<TvItemVO> items = new ArrayList<>();
                DbGuide dbGuide = tvProgramService.getDbGuide(dbChannel.getGuideId());
                if (dbGuide != null && dbGuide.getItems() != null) {
                    for (DbTvItem item : dbGuide.getItems(filter)) {
                        items.add(new TvItemVO(item));
                    }
                    channelVO.setItems(items);
                }
                gson.toJson(channelVO, channelVO.getClass(), jsonWriter);
            }
            jsonWriter.endArray();

            jsonWriter.endObject();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    @GET
    @Path("json")
    @Produces(MediaType.APPLICATION_JSON)
    public Response parseJson(@Context final HttpServletRequest request) throws InterruptedException {
        StreamingOutput streamingOutput = outputStream -> {
            try {
                printJson(new GZIPOutputStream(outputStream, true), filterFactory.createFilter(request.getParameterMap()), false);
            } catch (InterruptedException e) {
                // nothing to do
            }
        };
        return Response.ok(streamingOutput).
                header("Content-Encoding", "gzip").
                header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename = xmltv.json").build();
    }

    @GET
    @Path("json-test")
    @Produces(MediaType.TEXT_PLAIN + ";charset=utf-8")
    public Response parseJsonTest(@Context final HttpServletRequest request) throws InterruptedException {
        StreamingOutput streamingOutput = outputStream -> {
            try {
                printJson(outputStream, filterFactory.createFilter(request.getParameterMap()), true);
            } catch (InterruptedException e) {
                // nothing to do
            }
        };
        return Response.ok(streamingOutput).build();
    }

}
