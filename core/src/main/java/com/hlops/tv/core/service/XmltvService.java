package com.hlops.tv.core.service;

import java.io.OutputStream;
import java.util.Map;

/**
 * Created by tom on 4/4/15.
 */
public interface XmltvService {

    Map<String, String> getAllChannels() throws InterruptedException;

    void printXmltv(OutputStream outputStream, Filter filter) throws InterruptedException;

    void printJson(OutputStream outputStream, Filter filter, boolean beautify) throws InterruptedException;
}
