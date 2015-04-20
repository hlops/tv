package com.hlops.tv.core.service.impl;

import com.hlops.tasker.QueueService;
import com.hlops.tv.core.bean.ExtInf;
import com.hlops.tv.core.bean.db.DbChannel;
import com.hlops.tv.core.service.Filter;
import com.hlops.tv.core.service.MapDBService;
import com.hlops.tv.core.service.TVProgramService;
import com.hlops.tv.core.service.XmltvService;
import com.hlops.tv.core.task.DownloadXmltvTask;
import com.sun.xml.internal.stream.events.StartElementEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mapdb.BTreeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.namespace.QName;
import javax.xml.stream.*;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.util.HashMap;
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

    @Autowired
    private MapDBService dbService;

    @Autowired
    private TVProgramService tvProgramService;

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
    public void printXmltv(OutputStream out, final Filter filter) throws InterruptedException {
        try {
            InputStream in = new BufferedInputStream(new GZIPInputStream(new FileInputStream(getXmltvFile())));

            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();

            XMLStreamReader reader = inputFactory.createXMLStreamReader(in);

            final Map<String, Map<String, String>> filterData = new HashMap<String, Map<String, String>>();
            BTreeMap<String, DbChannel> dbChannels = dbService.getChannels();
            final Map<String, String> channelNames = new HashMap<String, String>();
            for (ExtInf extInf : tvProgramService.loadTV().getItems()) {
                String channelId = extInf.get(ExtInf.Attribute.tvg_name);
                DbChannel dbChannel = dbChannels.get(channelId);
                if (dbChannel != null && StringUtils.isNotEmpty(dbChannel.getXmltv())) {
                    Map<String, String> data = new HashMap<String, String>();
                    filterData.put(dbChannel.getXmltv(), data);
                    data.put("enabled", Boolean.toString(dbChannel.isEnabled()));
                    data.put("group", extInf.get(ExtInf.Attribute.group_title));

                    channelNames.put(dbChannel.getXmltv(), extInf.getName());
                }
            }

            StreamFilter staxFilter = new StreamFilter() {
                boolean isVisible = true;

                @Override
                public boolean accept(XMLStreamReader reader) {
                    if (reader.hasName()) {
                        String localPart = reader.getName().getLocalPart();
                        switch (reader.getEventType()) {
                            case XMLStreamConstants.START_ELEMENT:
                                if ("channel".equals(localPart)) {
                                    String xmltvId = reader.getAttributeValue(0);
                                    isVisible = filterData.containsKey(xmltvId) && filter.accept(filterData.get(xmltvId));
                                } else if ("programme".equals(localPart)) {
                                    String xmltvId = reader.getAttributeValue(2);
                                    Map<String, String> map = new HashMap<String, String>();
                                    if (filterData.containsKey(xmltvId)) {
                                        map.putAll(filterData.get(xmltvId));
                                        map.put("time-from")
                                    } else {
                                        isVisible = false;
                                    }
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
            XMLEventWriter writer = outputFactory.createXMLEventWriter(out);
            XMLEventFactory eventFactory = XMLEventFactory.newInstance();
            String tagName = null, channelId = null;
            while (reader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                if (event.getEventType() == XMLStreamConstants.CHARACTERS) {
                    if ("display-name".equals(tagName)) {
                        String name = channelNames.get(channelId);
                        if (name != null) {
                            event = eventFactory.createCharacters(name);
                        }
                    }
                    if (event.asCharacters().isWhiteSpace()) {
                        continue;
                    }
                } else if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
                    StartElementEvent el = ((StartElementEvent) event);
                    tagName = el.nameAsString();
                    if ("channel".equals(tagName)) {
                        channelId = el.getAttributeByName(new QName("id")).getValue();
                    }
                } else {
                    tagName = null;
                }

                writer.add(event);
            }
            eventReader.close();
        } catch (IOException e) {
            log.log(Level.ERROR, e.getMessage(), e);
        } catch (XMLStreamException e) {
            log.log(Level.ERROR, e.getMessage(), e);
        }
    }

}
