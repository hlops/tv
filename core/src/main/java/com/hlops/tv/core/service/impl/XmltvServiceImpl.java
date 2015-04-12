package com.hlops.tv.core.service.impl;

import com.hlops.tasker.QueueService;
import com.hlops.tv.core.service.Filter;
import com.hlops.tv.core.service.XmltvService;
import com.hlops.tv.core.task.DownloadXmltvTask;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.stream.*;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

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

    private File getXmltvFile() throws InterruptedException {
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
    public Map<String, String> getAllChannels() throws InterruptedException {
        Map<String, String> channels = new LinkedHashMap<String, String>();
        try {
            InputStream in = new BufferedInputStream(new GZIPInputStream(new FileInputStream(getXmltvFile())));
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
                                channels.put(channelId, reader.getElementText());
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
        } catch (IOException e) {
            log.log(Level.ERROR, e.getMessage(), e);
        } catch (XMLStreamException e) {
            log.log(Level.ERROR, e.getMessage(), e);
        }

        return channels;
    }

    @Override
    public void getXmltv(OutputStream out, Filter filter) throws InterruptedException {
        try {
            InputStream in = new BufferedInputStream(new GZIPInputStream(new FileInputStream(getXmltvFile())));

            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();

            XMLStreamReader reader = inputFactory.createXMLStreamReader(in);
            StreamFilter staxFilter = new StreamFilter() {
                boolean isVisible = true;

                @Override
                public boolean accept(XMLStreamReader reader) {
                    if (reader.hasName()) {
                        String localPart = reader.getName().getLocalPart();
                        switch (reader.getEventType()) {
                            case XMLStreamConstants.START_ELEMENT:
                                if ("channel".equals(localPart)) {
                                    isVisible = "1".equals(reader.getAttributeValue(0)) || "3".equals(reader.getAttributeValue(0));
                                } else if ("programme".equals(localPart)) {
                                    isVisible = "1".equals(reader.getAttributeValue(2));
                                }
                                break;
                            case XMLStreamConstants.END_ELEMENT: {
                                if ("channel".equals(localPart) || "programme".equals(localPart)) {
                                    boolean oldValue = isVisible;
                                    isVisible = true;
                                    return oldValue;
                                }
                                break;
                            }
                        }
                    }
                    return isVisible;
                }
            };
            reader = inputFactory.createFilteredReader(reader, staxFilter);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(reader);
            XMLEventWriter writer = outputFactory.createXMLEventWriter(new GZIPOutputStream(out, true));
            while (reader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                if (event.getEventType() == XMLStreamConstants.CHARACTERS) {
                    if (event.asCharacters().isWhiteSpace()) {
                        continue;
                    }
                }
                writer.add(event);
            }
            writer.close();
            eventReader.close();
        } catch (IOException e) {
            log.log(Level.ERROR, e.getMessage(), e);
        } catch (XMLStreamException e) {
            log.log(Level.ERROR, e.getMessage(), e);
        }
    }

}
