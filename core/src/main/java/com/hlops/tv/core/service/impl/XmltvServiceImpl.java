package com.hlops.tv.core.service.impl;

import com.hlops.tasker.QueueService;
import com.hlops.tv.core.service.XmltvService;
import com.hlops.tv.core.task.DownloadXmltvTask;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.zip.GZIPInputStream;

/**
 * Created by tom on 4/4/15.
 */
@Service
public class XmltvServiceImpl implements XmltvService {

    private static Logger log = LogManager.getLogger(XmltvServiceImpl.class);

    @Value("${tv-xmltv-url}")
    private String xmltvUrl;

    @Value("${tv-xmltv-file}")
    private String xmltvFile;

    @Autowired
    private QueueService queueService;

    private File getXmltv() throws InterruptedException {
        File file = new File(xmltvFile);
        Future<Void> booleanFuture = queueService.executeTask(new DownloadXmltvTask(file, xmltvUrl));
        try {
            booleanFuture.get();
        } catch (ExecutionException e) {
            log.log(Level.ERROR, e.getMessage(), e);
        }
        return file;
    }

    @Override
    public Map<String, String> getChannels() throws InterruptedException {
        Map<String, String> channels = new LinkedHashMap<String, String>();
        try {
            InputStream in = new BufferedInputStream(new GZIPInputStream(new FileInputStream(getXmltv())));
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory.createXMLStreamReader(in);
            String channelId = null;
            while (reader.hasNext()) {
                switch (reader.next()) {
                    case XMLStreamConstants.START_ELEMENT: {
                        if ("channel".equals(reader.getName().getLocalPart())) {
                            channelId = reader.getAttributeValue(0);
                        } else if ("display-name".equals(reader.getName().getLocalPart())) {
                            if (channelId != null) {
                                channels.put(reader.getElementText(), channelId);
                            }
                        }
                        break;
                    }
                    case XMLStreamConstants.END_ELEMENT: {
                        if ("channel".equals(reader.getName().getLocalPart())) {
                            channelId = null;
                        }
                        break;
                    }
                }
            }
            reader.close();
            System.out.println("done");
        } catch (IOException e) {
            log.log(Level.ERROR, e.getMessage(), e);
        } catch (XMLStreamException e) {
            log.log(Level.ERROR, e.getMessage(), e);
        }

        return channels;
    }

}
