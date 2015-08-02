package com.hlops.tv.core.service.impl;

import com.hlops.tasker.QueueService;
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

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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

    @Override
    public DbChannel[] getChannels(Filter filter) {
        Collection<DbChannel> channels = dbService.getChannels().values();
        Map<String, Integer> channelGroups = dbService.getChannelGroups();
        return channels.stream().filter(p -> p.applyFilter(filter)).sorted((gc1, gc2) -> {
            Integer gOrder1 = channelGroups.get(gc1.getGroup());
            Integer gOrder2 = channelGroups.get(gc2.getGroup());
            //noinspection NumberEquality
            if (gOrder1 != gOrder2) {
                if (gOrder1 == null) return 1;
                if (gOrder2 == null) return -1;
            } else if (gOrder1 == null) return 0;
            int res = Integer.compare(gOrder1, gOrder2);
            if (res == 0) {
                res = gc1.getTvgName().compareToIgnoreCase(gc2.getTvgName());
            }
            return res;
        }).toArray(DbChannel[]::new);
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

    @Override
    public List<String> getGroups() {
        return dbService.getChannelGroups().entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public void saveGroups(List<String> groups) {
        //noinspection SynchronizeOnNonFinalField
        synchronized (dbService) {
            Map<String, Integer> map = new HashMap<>();
            for (String group : groups) {
                map.put(group, map.size());
            }
            dbService.getChannelGroups().putAll(map);
        }
        dbService.commit();
    }

}
