package com.hlops.tv.rest;

import com.hlops.tv.core.bean.M3U;
import com.hlops.tv.core.service.TVProgramService;
import com.hlops.tv.core.service.impl.HtmlFilterFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * Created by tom on 3/27/15.
 */
@Path("/")
@Component
public class TvResource {

    @Autowired
    TVProgramService tvProgramService;

    @Autowired
    HtmlFilterFactory filterFactory;

    @GET
    @Path("playlist")
    @Produces(MediaType.TEXT_PLAIN + ";charset=utf-8")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED+ ";charset=utf-8")
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


}
