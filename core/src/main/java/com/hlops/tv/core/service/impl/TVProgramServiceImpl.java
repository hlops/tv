package com.hlops.tv.core.service.impl;

import com.hlops.tasker.QueueService;
import com.hlops.tv.core.bean.M3U;
import com.hlops.tv.core.service.TVProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
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
    private String encoding = "UTF-8";

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
}
