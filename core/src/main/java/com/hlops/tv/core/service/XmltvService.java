package com.hlops.tv.core.service;

import java.io.File;
import java.io.OutputStream;
import java.util.Map;

/**
 * Created by tom on 4/4/15.
 */
public interface XmltvService {

    File getFile();

    String getUrl();

    boolean isProgramBindingDirty();

    void setProgramBindingDirty(boolean value);

    Map<String, String> getAllChannels() throws InterruptedException;

    void printXmltv(OutputStream outputStream, Filter filter) throws InterruptedException;

}
