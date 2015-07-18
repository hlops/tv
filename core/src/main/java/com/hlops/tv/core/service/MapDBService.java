package com.hlops.tv.core.service;

import com.hlops.tv.core.bean.db.DbChannel;
import com.hlops.tv.core.bean.db.DbGuide;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by tom on 3/31/15.
 */
public interface MapDBService {

    void commit();

    ConcurrentMap<String, DbChannel> getChannels();

    ConcurrentMap<String, DbGuide> getGuideChannels();

    Map<String, Integer> getChannelGroups();

    void addChannelGroup(String group);

    void drop();

}
