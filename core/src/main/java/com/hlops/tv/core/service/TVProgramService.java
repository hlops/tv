package com.hlops.tv.core.service;

import com.hlops.tv.core.bean.M3U;

/**
 * Created by IntelliJ IDEA.
 * User: akarnachuk
 * Date: 7/31/14
 * Time: 1:55 PM
 */
public interface TVProgramService {

    String getPlaylistUrl();

    M3U loadTV();
}
