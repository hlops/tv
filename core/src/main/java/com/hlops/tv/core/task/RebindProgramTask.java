package com.hlops.tv.core.task;

import com.hlops.tasker.task.CacheableTask;
import com.hlops.tasker.task.impl.TaskImpl;
import com.hlops.tv.core.bean.db.DbChannel;
import com.hlops.tv.core.bean.db.DbGuide;
import com.hlops.tv.core.bean.db.DbTvItem;
import com.hlops.tv.core.service.MapDBService;
import com.hlops.tv.core.service.XmltvService;
import com.sun.xml.internal.stream.events.EndElementEvent;
import com.sun.xml.internal.stream.events.StartElementEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * Created by IntelliJ IDEA.
 * User: akarnachuk
 * Date: 6/17/15
 * Time: 4:08 PM
 */
public class RebindProgramTask extends TaskImpl<Void> implements CacheableTask<Void> {

    private static Logger log = LogManager.getLogger(RebindProgramTask.class);

    private MapDBService dbService;
    private XmltvService xmltvService;

    public RebindProgramTask(MapDBService dbService, XmltvService xmltvService) {
        this.dbService = dbService;
        this.xmltvService = xmltvService;
    }

    @Override
    public Object getId() {
        return getClass();
    }

    @Override
    public long getAliveTime() {
        return 0;
    }

    class ChannelWrapper {
        final boolean isActive;
        final List<DbTvItem> items = new ArrayList<>();
        final DbGuide guide = new DbGuide();

        public ChannelWrapper(String channelId, boolean isActive) {
            this.isActive = isActive;
            guide.setId(channelId);
        }
    }

    @Override
    public Void call() throws Exception {

        Map<String, ChannelWrapper> channels = new HashMap<>();

        for (DbChannel channel : dbService.getChannels().values()) {
            if (channel.getGuideId() != null) {
                channels.put(channel.getGuideId(), new ChannelWrapper(channel.getGuideId(), true));
            }
        }

        InputStream in = new BufferedInputStream(new GZIPInputStream(new FileInputStream(xmltvService.getFile())));
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLStreamReader reader = inputFactory.createXMLStreamReader(in);
        reader = inputFactory.createFilteredReader(reader, filter -> true);
        XMLEventReader eventReader = inputFactory.createXMLEventReader(reader);
        String tagName = null;
        DbTvItem tvItem = null;
        DbGuide guide = null;

        while (reader.hasNext()) {
            final XMLEvent event = eventReader.nextEvent();
            if (event.getEventType() == XMLStreamConstants.CHARACTERS) {
                if (tvItem != null) {
                    if ("title".equals(tagName)) {
                        tvItem.setTitle(event.asCharacters().getData());
                    } else if ("desc".equals(tagName)) {
                        tvItem.setDescription(event.asCharacters().getData());
                    } else if ("category".equals(tagName)) {
                        tvItem.setCategory(event.asCharacters().getData());
                    }
                }
                if (guide != null) {
                    if ("display-name".equals(tagName)) {
                        guide.setName(event.asCharacters().getData());
                    }
                }
                continue;
            } else if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
                StartElementEvent el = ((StartElementEvent) event);
                tagName = el.nameAsString();
                if ("channel".equals(tagName)) {
                    String channelId = el.getAttributeByName(new QName("id")).getValue();
                    channels.computeIfAbsent(channelId, s -> new ChannelWrapper(channelId, false));
                    guide = channels.get(channelId).guide;
                } else if ("programme".equals(tagName)) {
                    tvItem = new DbTvItem();
                    //noinspection unchecked
                    Iterator<Attribute> attributes = ((StartElement) event).getAttributes();
                    while (attributes.hasNext()) {
                        Attribute attr = attributes.next();
                        String localPart = attr.getName().getLocalPart();
                        String value = attr.getValue();
                        if ("start".equals(localPart)) {
                            tvItem.setStart(value);
                        } else if ("stop".equals(localPart)) {
                            tvItem.setStop(value);
                        } else if ("channel".equals(localPart)) {
                            if (!channels.containsKey(value)) {
                                log.warn("Don defined channel id: " + value);
                            }
                            if (channels.get(value).isActive) {
                                channels.get(value).items.add(tvItem);
                            } else {
                                tvItem = null;
                                break;
                            }
                        }
                    }
                } else if ("icon".equals(tagName)) {
                    assert guide != null;
                    guide.setLogo(el.getAttributeByName(new QName("src")).getValue());
                }
                continue;
            } else if (event.getEventType() == XMLStreamConstants.END_ELEMENT) {
                EndElementEvent el = ((EndElementEvent) event);
                tagName = el.nameAsString();
                if ("channel".equals(tagName) || "programme".equals(tagName)) {

                }
            }
            tagName = null;
        }
        eventReader.close();

        xmltvService.setProgramBindingDirty(false);
        return null;
    }

}