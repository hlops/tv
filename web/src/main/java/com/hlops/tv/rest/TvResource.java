package com.hlops.tv.rest;

import com.hlops.tv.core.bean.M3U;
import com.hlops.tv.core.service.MapDBService;
import com.hlops.tv.core.service.TVProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
    MapDBService dbService;

    @GET
    @Path("playlist")
    @Produces(MediaType.TEXT_PLAIN + ";charset=utf-8")
    public String parsePlaylist() {
        M3U m3U = tvProgramService.loadTV();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        m3U.save(new PrintStream(out));
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
