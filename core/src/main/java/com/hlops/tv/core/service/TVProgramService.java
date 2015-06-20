package com.hlops.tv.core.service;

import com.hlops.tv.core.bean.M3U;
import com.hlops.tv.core.bean.db.DbChannel;
import com.hlops.tv.core.bean.db.DbGuide;
import com.hlops.tv.core.exception.BusinessException;

import java.io.PrintStream;

/**
 * Created by IntelliJ IDEA.
 * User: akarnachuk
 * Date: 7/31/14
 * Time: 1:55 PM
 */
public interface TVProgramService {

    void loadChannels() throws InterruptedException, BusinessException;

    void print(M3U m3u, PrintStream out, Filter filter);

    DbChannel[] getChannels(Filter filter);

    DbGuide[] getGuideChannels(Filter filter);
}
