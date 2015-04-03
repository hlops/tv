package com.hlops.tv.core.service.impl;

import com.hlops.tasker.QueueService;
import com.hlops.tv.core.bean.ExtInf;
import com.hlops.tv.core.bean.M3U;
import com.hlops.tv.core.bean.db.DbChannel;
import com.hlops.tv.core.service.MapDBService;
import com.hlops.tv.core.service.TVProgramService;
import com.hlops.tv.core.task.DownloadPlaylistTask;
import org.mapdb.BTreeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Collection;
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

    @Autowired
    private QueueService queueService;

    @Value("${tv-playlist-url}")
    private String playlist;

    @Value("${tv-playlist-encoding}")
    private String encoding;

    @Value("${tv-playlist-udp-prefix}")
    private String udpPrefix;

    @Autowired
    private MapDBService dbService;

    @Override
    public String getPlaylistUrl() {
        return playlist;
    }

    @Override
    public M3U loadTV() {
        Future<M3U> future = queueService.executeTask(new DownloadPlaylistTask(playlist, Charset.forName(encoding)));

        M3U playlist = null;
        try {
            playlist = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return playlist;
    }

    @Override
    public void parseChannels(M3U m3U) {
        BTreeMap<String, DbChannel> channels = dbService.getChannels();
        for (ExtInf item : m3U.getItems()) {
            DbChannel dbChannel = new DbChannel();
            if (channels.putIfAbsent(item.get(ExtInf.Attribute.tvg_name), dbChannel) == null) {
                dbChannel.parse(item);
            }
        }
        dbService.commit();
    }

    public void save(M3U m3u, PrintStream out) {
        out.print("#EXTM3U");
        for (Map.Entry<String, String> entry : m3u.getAttributes().entrySet()) {
            out.print(" " + entry.getKey() + "=\"" + entry.getValue() + "\"");
        }
        out.println();
        out.println();

        String group = "";
        for (ExtInf item : m3u.getItems()) {
            out.print("#EXTINF:" + item.getDuration());
            for (Map.Entry<String, String> entry : item.getAttributes().entrySet()) {
                if (entry.getKey().equals(ExtInf.Attribute.group_title.getAttributeName())) {
                    if (group.equals(entry.getValue())) {
                        continue;
                    }
                    group = entry.getValue();
                }
                out.print(" " + entry.getKey() + "=\"" + entry.getValue() + "\"");
            }
            out.print(", " + item.getName());
            out.println();

            out.println(udpPrefix + item.getUrl().substring(7));
        }
    }

}
