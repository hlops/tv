package com.hlops.tv.core.service;

import com.hlops.tv.core.bean.M3U;
import com.hlops.tv.core.bean.db.DbChannel;

import java.io.PrintStream;

/**
 * Created by IntelliJ IDEA.
 * User: akarnachuk
 * Date: 7/31/14
 * Time: 1:55 PM
 */
public interface TVProgramService {

    M3U loadTV() throws InterruptedException;

    void print(M3U m3u, PrintStream out);

}
