package com.hlops.tv.core.service.impl;

import com.hlops.tv.core.bean.db.DbChannel;
import com.hlops.tv.core.service.MapDBService;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * Created by tom on 3/31/15.
 */
@Service
public class MapDBServiceImpl implements MapDBService {

    @Value("${tv-playlist-storage}")
    private String storage;

    private DB db;

    @PostConstruct
    public void init() {
        db = DBMaker.newFileDB(new File(storage)).closeOnJvmShutdown().make();
    }

    @Override
    public void commit() {
        db.commit();
    }

    @Override
    public BTreeMap<String, DbChannel> getChannels() {
        BTreeMap<String, DbChannel> map = db.getTreeMap(DbChannel.NAME);
        return map;
    }

}
