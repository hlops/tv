package com.hlops.tv.core.service;

import com.hlops.tv.core.bean.M3U;

import java.nio.charset.Charset;

/**
 * Created by IntelliJ IDEA.
 * User: akarnachuk
 * Date: 6/17/15
 * Time: 2:02 PM
 */
public interface M3uService {

    String getPlaylist();

    String getUdpPrefix();

    Charset getEncoding();

    /**
     * Updates db with m3u channels
     *
     * @param m3u M3U channels data
     */
    void parseChannels(M3U m3u);
}
