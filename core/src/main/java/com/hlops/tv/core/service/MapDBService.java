package com.hlops.tv.core.service;

import com.hlops.tv.core.bean.db.DbChannel;
import org.mapdb.BTreeMap;

/**
 * Created by tom on 3/31/15.
 */
public interface MapDBService {

    void commit();

    BTreeMap<String, DbChannel> getChannels();

}
