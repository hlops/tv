package com.hlops.tv.core.service;

import com.hlops.tv.core.bean.db.DbChannel;

import java.util.concurrent.ConcurrentMap;

/**
 * Created by tom on 3/31/15.
 */
public interface MapDBService {

    void commit();

    ConcurrentMap<String, DbChannel> getChannels();
}
