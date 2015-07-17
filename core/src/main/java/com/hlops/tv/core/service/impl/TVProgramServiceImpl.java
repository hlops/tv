package com.hlops.tv.core.service.impl;

import com.hlops.tasker.QueueService;
import com.hlops.tv.core.bean.M3U;
import com.hlops.tv.core.bean.db.DbChannel;
import com.hlops.tv.core.bean.db.DbGuide;
import com.hlops.tv.core.exception.BusinessException;
import com.hlops.tv.core.service.*;
import com.hlops.tv.core.task.DownloadM3uTask;
import com.hlops.tv.core.task.DownloadXmltvTask;
import com.hlops.tv.core.task.RebindProgramTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.PrintStream;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

/**
 * Created by IntelliJ IDEA.
 * User: akarnachuk
 * Date: 7/31/14
 * Time: 2:13 PM
 */

@Service
public class TVProgramServiceImpl implements TVProgramService {

    private static Logger log = LogManager.getLogger(TVProgramServiceImpl.class);

    @Value("${tv-xmltv-url}")
    private String xmltvUrl;

    @Value("${tv-xmltv-file}")
    private String xmltvFile;

    @Autowired
    private QueueService queueService;

    @Autowired
    private MapDBService dbService;

    @Autowired
    private M3uService m3uService;

    @Autowired
    private XmltvService xmltvService;

    @Override
    public void loadChannels() throws InterruptedException, BusinessException {
        try {
            queueService.executeTask(new DownloadM3uTask(m3uService)).get();
            queueService.executeTask(new DownloadXmltvTask(xmltvService)).get();
            if (xmltvService.isProgramBindingDirty()) {
                queueService.executeTask(new RebindProgramTask(dbService, xmltvService)).get();
            }
        } catch (ExecutionException e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("Can't load m3u channels", e);
        }
    }

    public void print(M3U m3u, PrintStream out, Filter filter) {
/*
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
            if (filter.accept(prepare(item, dbChannel))) {
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
                        //value = dbChannel.getXmltv();
                    }
                    out.print(" " + entry.getKey() + "=\"" + value + "\"");
                }
                out.print(", " + item.getName());
                out.println();

                //out.println(m3uUdpPrefix + item.getUrl().substring(7));
            }
        }
*/
    }

    @Override
    public DbChannel[] getChannels(Filter filter) {
        Collection<DbChannel> channels = dbService.getChannels().values();
        return channels.stream().filter(p -> p.applyFilter(filter)).toArray(DbChannel[]::new);
    }

    @Override
    public DbGuide getDbGuide(String id) {
        return dbService.getGuideChannels().get(id);
    }

    @Override
    public DbGuide[] getGuideChannels(Filter filter) {
        Collection<DbGuide> guides = dbService.getGuideChannels().values();
        return guides.stream().filter(p -> p.applyFilter(filter)).toArray(DbGuide[]::new);
    }

}
