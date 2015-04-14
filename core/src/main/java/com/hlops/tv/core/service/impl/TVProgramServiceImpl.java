package com.hlops.tv.core.service.impl;

import com.hlops.tasker.QueueService;
import com.hlops.tv.core.bean.ExtInf;
import com.hlops.tv.core.bean.M3U;
import com.hlops.tv.core.bean.db.DbChannel;
import com.hlops.tv.core.service.Filter;
import com.hlops.tv.core.service.MapDBService;
import com.hlops.tv.core.service.TVProgramService;
import com.hlops.tv.core.task.DownloadPlaylistTask;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mapdb.BTreeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by IntelliJ IDEA.
 * User: akarnachuk
 * Date: 7/31/14
 * Time: 2:13 PM
 */

@Service
public class TVProgramServiceImpl implements TVProgramService {

    private static Logger log = LogManager.getLogger(TVProgramServiceImpl.class);

    @Value("${tv-playlist-url}")
    private String playlist;

    @Value("${tv-playlist-encoding}")
    private String encoding;

    @Value("${tv-playlist-udp-prefix}")
    private String udpPrefix;

    @Autowired
    private QueueService queueService;

    @Autowired
    HtmlFilterFactory filterFactory;

    @Autowired
    private MapDBService dbService;

    private M3U getM3U() throws InterruptedException {
        Future<M3U> future = queueService.executeTask(new DownloadPlaylistTask(playlist, Charset.forName(encoding)));

        M3U playlist = null;
        try {
            playlist = future.get();
        } catch (ExecutionException e) {
            log.log(Level.ERROR, e.getMessage(), e);
        }

        return playlist;
    }

    @Override
    public M3U loadTV() throws InterruptedException {
        M3U m3U = getM3U();
        parseChannels(m3U);
        return m3U;
    }

    private void parseChannels(M3U m3U) {
        BTreeMap<String, DbChannel> channels = dbService.getChannels();
        for (ExtInf item : m3U.getItems()) {
            DbChannel dbChannel = new DbChannel();
            if (channels.putIfAbsent(item.get(ExtInf.Attribute.tvg_name), dbChannel) == null) {
                dbChannel.parse(item);
            }
        }
        dbService.commit();
    }

    public void print(M3U m3u, PrintStream out, Filter filter) {
        out.print("#EXTM3U");
        for (Map.Entry<String, String> entry : m3u.getAttributes().entrySet()) {
            out.print(" " + entry.getKey() + "=\"" + entry.getValue() + "\"");
        }
        out.println();
        out.println();

        String group = "";
        BTreeMap<String, DbChannel> channels = dbService.getChannels();
        for (ExtInf item : m3u.getItems()) {
            DbChannel dbChannel = channels.get(item.get(ExtInf.Attribute.tvg_name));
            if (filter.accept(filterFactory.prepare(item, dbChannel))) {
                out.print("#EXTINF:" + item.getDuration());
                for (Map.Entry<String, String> entry : item.getAttributes().entrySet()) {
                    String value = entry.getValue();
                    if (entry.getKey().equals(ExtInf.Attribute.group_title.getAttributeName())) {
                        if (group.equals(value)) {
                            continue;
                        }
                        group = value;
                    }
                    if (entry.getKey().equals(ExtInf.Attribute.tvg_name.getAttributeName())) {
                        value = dbChannel.getXmltv();
                    }
                    out.print(" " + entry.getKey() + "=\"" + value + "\"");
                }
                out.print(", " + item.getName());
                out.println();

                out.println(udpPrefix + item.getUrl().substring(7));
            }
        }
    }

}
