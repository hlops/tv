package com.hlops.tv.rest;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import com.hlops.tv.core.bean.db.DbChannel;
import com.hlops.tv.core.bean.db.DbGuide;
import com.hlops.tv.core.bean.db.DbTvItem;
import com.hlops.tv.core.exception.BusinessException;
import com.hlops.tv.core.service.Filter;
import com.hlops.tv.core.service.M3uService;
import com.hlops.tv.core.service.TVProgramService;
import com.hlops.tv.core.service.XmltvService;
import com.hlops.tv.core.service.impl.filter.HtmlFilterFactory;
import com.hlops.tv.model.ChannelVO;
import com.hlops.tv.model.TvItemVO;

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
    M3uService m3uService;

    @Autowired
    HtmlFilterFactory filterFactory;
    private HttpServletRequest request;

    @Context
    private void setContext(HttpServletRequest request) {
        this.request = request;
        try {
            tvProgramService.loadChannels();
        } catch (BusinessException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @GET
    @Path("playlist")
    @Produces(MediaType.TEXT_PLAIN + ";charset=utf-8")
    public Response parsePlaylist() throws InterruptedException {
        StreamingOutput streamingOutput = outputStream -> {
            m3uService.print(tvProgramService.getChannels(filterFactory.createFilter(request.getParameterMap())), new PrintStream(outputStream));
        };
        return Response.ok(streamingOutput).build();
    }

    @GET
    @Path("playlist.m3u")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response parsePlaylistFile() throws InterruptedException {
        StreamingOutput streamingOutput = outputStream -> {
            m3uService.print(tvProgramService.getChannels(filterFactory.createFilter(request.getParameterMap())), new PrintStream(outputStream));
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
    public Response parseXmltv() throws InterruptedException {
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
    public Response parseXmltvTest() throws InterruptedException {
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
    public Response parseXmltvGz() throws InterruptedException {
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

            jsonWriter.name("groups").beginArray();
            for (String group : tvProgramService.getGroups()) {
                jsonWriter.value(group);
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
    public Response parseJson() throws InterruptedException, BusinessException {
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
    public Response parseJsonTest() throws InterruptedException, BusinessException {
        StreamingOutput streamingOutput = outputStream -> {
            try {
                printJson(outputStream, filterFactory.createFilter(request.getParameterMap()), true);
            } catch (InterruptedException e) {
                // nothing to do
            }
        };
        return Response.ok(streamingOutput).build();
    }

    @PUT
    @Path("groups")
    public Response saveGroups(List<String> groups) throws InterruptedException, BusinessException {
        tvProgramService.saveGroups(groups);
        return Response.ok().build();
    }

}
