package com.hlops.tv.core.task;

import com.hlops.tasker.task.CacheableTask;
import com.hlops.tasker.task.impl.TaskImpl;
import com.hlops.tv.core.bean.db.DbTvItem;
import com.hlops.tv.core.service.XmltvService;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Created by IntelliJ IDEA.
 * User: akarnachuk
 * Date: 6/17/15
 * Time: 4:08 PM
 */
public class RebindProgramTask extends TaskImpl<Void> implements CacheableTask<Void> {

    private static Logger log = LogManager.getLogger(RebindProgramTask.class);

    private XmltvService xmltvService;

    public RebindProgramTask(XmltvService xmltvService) {

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

    @Override
    public Void call() throws Exception {

        Map<String, String> channels = new HashMap<>();

        channels.put("1", "1");

        InputStream in = new BufferedInputStream(new GZIPInputStream(new FileInputStream(xmltvService.getXmltvFile())));
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLStreamReader reader = inputFactory.createXMLStreamReader(in);
        reader = inputFactory.createFilteredReader(reader, filter -> true);
        XMLEventReader eventReader = inputFactory.createXMLEventReader(reader);
        String tagName = null;
        DbTvItem tvItem = null;
        while (reader.hasNext()) {
            final XMLEvent event = eventReader.nextEvent();
            if (event.getEventType() == XMLStreamConstants.CHARACTERS) {
                if (tvItem != null) {
                    if ("title".equals(tagName)) {
                        tvItem.setTitle(event.asCharacters().getData());
                        System.out.println(event.asCharacters().getData());
                    } else if ("desc".equals(tagName)) {
                        tvItem.setDescription(event.asCharacters().getData());
                    } else if ("category".equals(tagName)) {
                        tvItem.setCategory(event.asCharacters().getData());
                    }
                }
                if ("display-name".equals(tagName)) {
                    //System.out.println(event.asCharacters().getData());
                }
            } else if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
                StartElementEvent el = ((StartElementEvent) event);
                tagName = el.nameAsString();
                if ("channel".equals(tagName)) {
                    //channelId = el.getAttributeByName(new QName("id")).getValue();
                    //channels.put(channelId, new JsonChannel());
                } else if ("programme".equals(tagName)) {
                    tvItem = new DbTvItem();
                    //noinspection unchecked
                    Iterator<Attribute> attributes = ((StartElement) event).getAttributes();
                    while (attributes.hasNext()) {
                        Attribute attr = attributes.next();
                        String localPart = attr.getName().getLocalPart();
                        if ("start".equals(localPart)) {
                            tvItem.setStart(attr.getValue());
                        } else if ("stop".equals(localPart)) {
                            tvItem.setStop(attr.getValue());
                        } else if ("channel".equals(localPart)) {
                            if (!channels.containsKey(attr.getValue())) {
                                tvItem = null;
                                break;
                            }
                        }
                    }
                    if (tvItem != null) {
                        //
                    }
                }
            } else {
                tagName = null;
            }

        }
        eventReader.close();


        xmltvService.setProgramBindingDirty(false);
        return null;
    }

}
