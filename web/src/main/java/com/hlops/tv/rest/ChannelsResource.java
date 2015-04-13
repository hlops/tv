package com.hlops.tv.rest;

import com.hlops.tv.core.bean.ExtInf;
import com.hlops.tv.core.bean.M3U;
import com.hlops.tv.core.bean.db.DbChannel;
import com.hlops.tv.core.service.MapDBService;
import com.hlops.tv.core.service.TVProgramService;
import com.hlops.tv.core.service.XmltvService;
import com.hlops.tv.model.ChannelVO;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mapdb.BTreeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tom on 4/4/15.
 */
@Path("/rest")
@Component
public class ChannelsResource {

    private static Logger log = LogManager.getLogger(ChannelsResource.class);

    @Autowired
    TVProgramService tvProgramService;

    @Autowired
    XmltvService xmltvService;

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

    private String filterKey(String key) {
        return key.replaceAll("[\\s-]", "").toLowerCase();
    }

    @GET
    @Path("channels")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ChannelVO> getChannels() throws InterruptedException {
        List<ChannelVO> result = new ArrayList<ChannelVO>();
        M3U m3U = tvProgramService.loadTV();
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
                if (bean.isEnabled() != null) {
                    dbChannel.setEnabled(bean.isEnabled());
                }
                channels.replace(bean.getId(), dbChannel);
                dbService.commit();
            } catch (IllegalAccessException e) {
                log.log(Level.ERROR, e.getMessage(), e);
            } catch (InvocationTargetException e) {
                log.log(Level.ERROR, e.getMessage(), e);
            }
        }
    }

    @GET
    @Path("xmltv-channels")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Map<String, String> getXmltvChannels() throws InterruptedException {
        return xmltvService.getAllChannels();
    }

    @GET
    @Path("xmltv-bind-channels")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public void bindChannels() throws InterruptedException {
        Map<String, String> channels = new HashMap<String, String>();
        for (Map.Entry<String, String> entry : xmltvService.getAllChannels().entrySet()) {
            channels.put(filterKey(entry.getValue()), entry.getKey());
        }
        boolean isModified = false;
        M3U m3U = tvProgramService.loadTV();
        BTreeMap<String, DbChannel> channelsMap = dbService.getChannels();
        for (ExtInf extInf : m3U.getItems()) {
            String channelId = extInf.get(ExtInf.Attribute.tvg_name);
            DbChannel dbChannel = channelsMap.get(channelId);
            if (StringUtils.isEmpty(dbChannel.getXmltv())) {
                String xmltvChannelId = channels.get(filterKey(extInf.getName()));
                if (xmltvChannelId == null) {
                    xmltvChannelId = channels.get(filterKey(extInf.getName() + " канал"));
                }
                dbChannel.setXmltv(xmltvChannelId);
                isModified = true;
                channelsMap.replace(channelId, dbChannel);
            }
        }
        if (isModified) {
            dbService.commit();
        }
    }

}
