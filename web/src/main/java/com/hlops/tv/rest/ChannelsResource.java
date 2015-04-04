package com.hlops.tv.rest;

import com.hlops.tv.core.bean.ExtInf;
import com.hlops.tv.core.bean.M3U;
import com.hlops.tv.core.bean.db.DbChannel;
import com.hlops.tv.core.service.MapDBService;
import com.hlops.tv.core.service.TVProgramService;
import com.hlops.tv.model.ChannelVO;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.mapdb.BTreeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tom on 4/4/15.
 */
@Path("/rest")
@Component
public class ChannelsResource {

    @Autowired
    TVProgramService tvProgramService;

    @Autowired
    private MapDBService dbService;

    private static BeanUtilsBean NOT_NULL_BEAN_UTILS = new BeanUtilsBean() {
        @Override
        public void copyProperty(Object bean, String name, Object value) throws IllegalAccessException, InvocationTargetException {
            if (value != null) {
                super.copyProperty(bean, name, value);
            }
        }
    };

    @GET
    @Path("channels")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ChannelVO> getChannels() {
        List<ChannelVO> result = new ArrayList<ChannelVO>();
        M3U m3U = tvProgramService.loadTV();
        tvProgramService.parseChannels(m3U);
        BTreeMap<String, DbChannel> channelsMap = dbService.getChannels();
        for (ExtInf extInf : m3U.getItems()) {
            DbChannel dbChannel = channelsMap.get(extInf.get(ExtInf.Attribute.tvg_name));
            result.add(new ChannelVO(extInf, dbChannel));
        }
        return result;
    }

    @PUT
    @Path("channel")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void save(ChannelVO bean) {
        BTreeMap<String, DbChannel> channels = dbService.getChannels();
        DbChannel dbChannel = channels.get(bean.getId());
        if (dbChannel != null) {
            try {
                NOT_NULL_BEAN_UTILS.copyProperties(dbChannel, bean);
                channels.replace(bean.getId(), dbChannel);
                dbService.commit();
            } catch (IllegalAccessException e) {
                // todo:
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
