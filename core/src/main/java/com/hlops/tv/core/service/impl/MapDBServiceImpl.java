package com.hlops.tv.core.service.impl;

import com.hlops.tv.core.bean.db.DbChannel;
import com.hlops.tv.core.bean.db.DbGuide;
import com.hlops.tv.core.service.MapDBService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by tom on 3/31/15.
 */
@Service
public class MapDBServiceImpl implements MapDBService {

    private static Logger log = LogManager.getLogger(MapDBServiceImpl.class);

    private static final String DB_CHANNELS = "dbChannels";
    private static final String DB_GUIDE_CHANNELS = "dbGuideChannels";

    @Value("${tv-playlist-storage}")
    private String storage;

    private DB db;

    @PostConstruct
    public void init() {
        File file = new File(storage);
        log.debug("init db: " + file.getAbsolutePath());
        //noinspection ResultOfMethodCallIgnored
        file.getParentFile().mkdirs();
        db = DBMaker.newFileDB(file).closeOnJvmShutdown().make();
    }

    @Override
    public void commit() {
        db.commit();
    }

    @Override
    public ConcurrentMap<String, DbChannel> getChannels() {
        return db.getHashMap(DB_CHANNELS);
    }

    @Override
    public ConcurrentMap<String, DbGuide> getGuideChannels() {
        return db.getHashMap(DB_GUIDE_CHANNELS);
    }

    @Override
    public void drop() {
        db = DBMaker.newFileDB(new File(storage)).deleteFilesAfterClose().make();
        db.close();
        init();
    }

}
