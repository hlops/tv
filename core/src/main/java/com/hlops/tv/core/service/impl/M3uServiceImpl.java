package com.hlops.tv.core.service.impl;

import com.hlops.tv.core.bean.ExtInf;
import com.hlops.tv.core.bean.M3U;
import com.hlops.tv.core.bean.db.DbChannel;
import com.hlops.tv.core.service.M3uService;
import com.hlops.tv.core.service.MapDBService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by IntelliJ IDEA.
 * User: akarnachuk
 * Date: 6/17/15
 * Time: 2:03 PM
 */
@Service
public class M3uServiceImpl implements M3uService {

    private static Logger log = LogManager.getLogger(M3uServiceImpl.class);

    public static final String M3U_UDP_PREFIX = "http://192.168.1.1:4000/udp/";

    @Value("${tv-playlist-url}")
    private String playlist;

    @Value("${tv-playlist-encoding}")
    private String encoding;

    @Value("${tv-playlist-udp-prefix}")
    private String udpPrefix;

    @Autowired
    private MapDBService dbService;

    private Charset charset;

    public String getPlaylist() {
        return playlist;
    }

    public String getUdpPrefix() {
        return udpPrefix;
    }

    public Charset getEncoding() {
        return charset;
    }

    @PostConstruct
    private void init() {
        charset = Charset.forName(encoding);
    }

    public void parseChannels(M3U m3u) {
        ConcurrentMap<String, DbChannel> channels = dbService.getChannels();
        for (ExtInf item : m3u.getItems()) {
            String id = item.get(ExtInf.Attribute.tvg_name);
            DbChannel channel = channels.computeIfAbsent(id, f -> createDbChannel(item));
            if (channel != null) {
                for (int i = 0; i < 5; i++) {
                    DbChannel updatedChannel = updateChannel(channel, item);
                    if (updatedChannel != null) {
                        if (!channels.replace(id, channel, updatedChannel)) {
                            log.warn("can't update channel " + id + " data from m3u. #" + (i + 1));
                            continue;
                        }
                    }
                    break;
                }
            }
        }
        dbService.commit();
    }

    private DbChannel updateChannel(DbChannel channel, ExtInf item) {
        if (!item.getName().equals(channel.getTvgName())
                || !item.getUrl().equals(channel.getUrl())) {
            DbChannel clone = channel.clone();
            clone.setTvgName(item.getName());
            clone.setUrl(item.getUrl());
            return clone;
        }
        return null;
    }

    private DbChannel createDbChannel(ExtInf item) {
        DbChannel channel = new DbChannel();
        channel.setTvgName(item.getName());
        channel.setUrl(item.getUrl());
        channel.setGroup(item.get(ExtInf.Attribute.group_title));
        return channel;
    }

    public void print(DbChannel[] channels, PrintStream out) {
        out.println("#EXTM3U m3uautoload=1 cache=500 deinterlace=1 aspect-ratio=4:3 crop=690x550+10+10");
        out.println();

        String group = "";
        for (DbChannel dbChannel : channels) {
            out.print("#EXTINF:-1");
            if (dbChannel.getGuideId() != null) {
                out.print(" tvg-name=\"" + dbChannel.getGuideId() + "\"");
            }
            if (dbChannel.hasAttribute(DbChannel.Attribute.hd)) {
                out.print(" crop=1920x1080+0+0");
            }
            if (dbChannel.hasAttribute(DbChannel.Attribute.wide)) {
                out.print(" aspect-ratio=16:9");
            }
            if (!group.equals(dbChannel.getGroup())) {
                out.print(" group-title=\"" + dbChannel.getGroup() + "\"");
                group = dbChannel.getGroup();
            }

            out.print(", " + dbChannel.getTvgName());
            out.println();

            //out.println("#EXTGRP:" + dbChannel.getGroup());
            out.println(M3U_UDP_PREFIX + dbChannel.getUrl().substring(7));
            out.println();
        }
    }
}
