package com.hlops.tv.rest;

import com.hlops.tv.core.bean.ExtInf;
import com.hlops.tv.core.bean.M3U;
import com.hlops.tv.core.bean.db.DbChannel;
import com.hlops.tv.core.service.MapDBService;
import com.hlops.tv.core.service.TVProgramService;
import com.hlops.tv.model.ChannelVO;
import org.mapdb.BTreeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tom on 4/4/15.
 */
@Path("/rest")
@Component
public class RestResource {

    @Autowired
    TVProgramService tvProgramService;

    @Autowired
    private MapDBService dbService;

    @GET
    @Path("channels")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ChannelVO> parsePlaylist() {
        List<ChannelVO> result = new ArrayList<ChannelVO>();
        M3U m3U = tvProgramService.loadTV();
        tvProgramService.parseChannels(m3U);
        BTreeMap<String, DbChannel> channelsMap = dbService.getChannels();
        for (ExtInf extInf : m3U.getItems()) {
            DbChannel dbChannel = channelsMap.get(extInf.get(ExtInf.Attribute.tvg_name));
            if (dbChannel.isEnabled()) {
                result.add(new ChannelVO(extInf, channelsMap.get(extInf.get(ExtInf.Attribute.tvg_name))));
            }
        }
        return result;
    }
}
