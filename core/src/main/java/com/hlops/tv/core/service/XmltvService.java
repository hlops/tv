package com.hlops.tv.core.service;

import java.util.Map;

/**
 * Created by tom on 4/4/15.
 */
public interface XmltvService {

    Map<String, String> getChannels() throws InterruptedException;
}
