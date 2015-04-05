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
import java.util.LinkedHashMap;
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

    @GET
    @Path("channels")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ChannelVO> getChannels() throws InterruptedException {
        List<ChannelVO> result = new ArrayList<ChannelVO>();
        M3U m3U = tvProgramService.loadTV();
        BTreeMap<String, DbChannel> channelsMap = dbService.getChannels();
        Map<String, String> xmltvMap = new LinkedHashMap<String, String>();
        for (Map.Entry<String, String> entry : xmltvService.getChannels().entrySet()) {
            xmltvMap.put(entry.getKey().replaceAll("[\\s-]", "").toLowerCase(), entry.getValue());
        }

        for (ExtInf extInf : m3U.getItems()) {
            DbChannel dbChannel = channelsMap.get(extInf.get(ExtInf.Attribute.tvg_name));
            if (StringUtils.isEmpty(dbChannel.getXmltv())) {
                dbChannel.setXmltv(xmltvMap.get(extInf.getName().replaceAll("[\\s-]", "").toLowerCase()));
            }
            ChannelVO channelVO = new ChannelVO(extInf, dbChannel);
            result.add(channelVO);
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
        return xmltvService.getChannels();
    }

}