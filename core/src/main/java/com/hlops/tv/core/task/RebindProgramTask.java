package com.hlops.tv.core.task;

import com.hlops.tasker.task.CacheableTask;
import com.hlops.tasker.task.impl.TaskImpl;
import com.hlops.tv.core.bean.db.DbChannel;
import com.hlops.tv.core.bean.db.DbGuide;
import com.hlops.tv.core.bean.db.DbTvItem;
import com.hlops.tv.core.service.MapDBService;
import com.hlops.tv.core.service.XmltvService;
import com.hlops.tv.core.service.impl.filter.TimeFormatter;
import com.sun.xml.internal.stream.events.StartElementEvent;
import org.apache.commons.lang3.StringUtils;
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
import java.util.concurrent.ConcurrentMap;
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
        final List<DbTvItem> items = new ArrayList<>();
        final DbGuide guide = new DbGuide();

        public ChannelWrapper(String channelId) {
            guide.setId(channelId);
        }
    }

    @Override
    public Void call() throws Exception {

        Map<String, ChannelWrapper> channels = new HashMap<>();

        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        try (
                InputStream in = new BufferedInputStream(new GZIPInputStream(new FileInputStream(xmltvService.getFile())));
        ) {
            XMLStreamReader reader = inputFactory.createXMLStreamReader(in);
            TimeFormatter formatter = new TimeFormatter();
            try {
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
                            channels.computeIfAbsent(channelId, s -> new ChannelWrapper(channelId));
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
                                    tvItem.setStart(formatter.format(value));
                                } else if ("stop".equals(localPart)) {
                                    tvItem.setStop(formatter.format(value));
                                } else if ("channel".equals(localPart)) {
                                    if (!channels.containsKey(value)) {
                                        log.warn("Unexpected programme channel : " + value);
                                    }
                                    channels.get(value).items.add(tvItem);
                                }
                            }
                        } else if ("icon".equals(tagName)) {
                            assert guide != null;
                            guide.setLogo(el.getAttributeByName(new QName("src")).getValue());
                        }
                        continue;
                    }
                    tagName = null;
                }
            } finally {
                reader.close();
            }
        }

        String startDate = TimeFormatter.formatDateWithShift(getActualStartDate());
        String endDate = TimeFormatter.formatDateWithShift(getActualEndDate());

        ConcurrentMap<String, DbGuide> guideChannels = dbService.getGuideChannels();
        Map<String, String> channelNames = new HashMap<>();
        for (ChannelWrapper wrapper : channels.values()) {
            Set<DbTvItem> items = new HashSet<>(wrapper.items);
            DbGuide guide = guideChannels.get(wrapper.guide.getId());
            if (guide != null) {
                items.addAll(Arrays.asList(guide.getItems()));
            }
            for (Iterator<DbTvItem> it = items.iterator(); it.hasNext(); ) {
                DbTvItem item = it.next();
                if (startDate.compareTo(item.getStart()) > 0 || endDate.compareTo(item.getStart()) < 0) {
                    log.debug(wrapper.guide.getName() + " deleted obsolete item: " + item.toString());
                    it.remove();
                }
            }

            wrapper.guide.setItems(items.toArray(new DbTvItem[items.size()]));
            guideChannels.put(wrapper.guide.getId(), wrapper.guide);

            addAssociatedNames(channelNames, wrapper.guide.getName(), wrapper.guide.getId());
        }

        for (DbChannel channel : dbService.getChannels().values()) {
            if (!Boolean.FALSE.equals(channel.isEnabled()) && channel.getGuideId() == null) {
                associateChannelWithGuide(channelNames, channel);
            }
            if (StringUtils.isNotBlank(channel.getGroup())) {
                dbService.addChannelGroup(channel.getGroup());
            }
        }

        dbService.commit();
        xmltvService.setProgramBindingDirty(false);
        return null;
    }

    private void addAssociatedNames(Map<String, String> channelNames, String name, String id) {
        for (String key : getAssociatedNames(name)) {
            String key1 = key.replaceAll("\\s{2,}", " ").replaceAll("\\(\\*\\)", "").trim().toUpperCase();
            channelNames.put(key1, id);
            channelNames.put(key1.replaceAll("КАНАЛ", "").trim(), id);
        }
    }

    private String[] getAssociatedNames(String name) {
        return new String[]{name};
    }

    private void associateChannelWithGuide(Map<String, String> names, DbChannel channel) {
        String id = names.get(channel.getTvgName().toUpperCase());
        if (id != null) {
            channel.setGuideId(id);
        }
    }

    private String getActualStartDate() {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DAY_OF_YEAR, -3);
        return TimeFormatter.DATE_FORMAT.format(instance.getTime());
    }

    private String getActualEndDate() {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DAY_OF_YEAR, 14);
        return TimeFormatter.DATE_FORMAT.format(instance.getTime());
    }

}