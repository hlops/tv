package com.hlops.tv.core.service.impl;

import com.hlops.tv.core.bean.db.DbChannel;
import com.hlops.tv.core.service.Filter;
import com.hlops.tv.core.service.MapDBService;
import com.hlops.tv.core.service.XmltvService;
import com.hlops.tv.core.service.impl.filter.TimeFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.xml.stream.*;
import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
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
    private String xmltvFileName;

    @Autowired
    private MapDBService dbService;

    private File xmltvFile;
    private boolean programBindingDirty;

    @PostConstruct
    private void init() {
        xmltvFile = new File(xmltvFileName);
    }

    public File getXmltvFile() {
        return xmltvFile;
    }

    public String getXmltvUrl() {
        return xmltvUrl;
    }

    @Override
    public boolean isProgramBindingDirty() {
        return programBindingDirty;
    }

    @Override
    public void setProgramBindingDirty(boolean value) {
        programBindingDirty = value;
    }

    @Override
    public void rebindProgram() {

    }

    public void parse(File file) {
        Map<String, String> channels = new LinkedHashMap<>();
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
        } catch (IOException | XMLStreamException e) {
            log.error(e.getMessage(), e);
        }
    }

    /*
            private File getXmltvFile() throws InterruptedException {
                File file = new File(xmltvFile);
                Future<Void> future = queueService.executeTask(new DownloadXmltvTask(file, xmltvUrl));
                try {
                    future.get();
                } catch (ExecutionException e) {
                    log.log(Level.ERROR, e.getMessage(), e);
                }
                return file;
            }

        */
    @Override
    public Map<String, String> getAllChannels() throws InterruptedException {
/*
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
        } catch (IOException | XMLStreamException e) {
            log.log(Level.ERROR, e.getMessage(), e);
        }
*/
        return null;
    }

    @Override
    public void printXmltv(OutputStream out, final Filter filter) throws InterruptedException {
/*
        try {
            MyStreamFilter staxFilter = new MyStreamFilter(filter);
            InputStream in = new BufferedInputStream(new GZIPInputStream(new FileInputStream(getXmltvFile())));

            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();

            XMLStreamReader reader = inputFactory.createXMLStreamReader(in);
            reader = inputFactory.createFilteredReader(reader, staxFilter);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(reader);
            XMLEventWriter writer = outputFactory.createXMLEventWriter(out);
            XMLEventFactory eventFactory = XMLEventFactory.newInstance();
            String tagName = null, channelId = null;
            while (reader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                if (event.getEventType() == XMLStreamConstants.CHARACTERS) {
                    if ("display-name".equals(tagName)) {
                        String name = staxFilter.getChannelNames().get(channelId);
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
                    } else if ("programme".equals(tagName)) {
                        StartElement startElement = (StartElement) event;
                        String xmltvId = ((StartElement) event).getAttributeByName(new QName("channel")).getValue();
                        List<Attribute> attributeList = new ArrayList<Attribute>();
                        for (//noinspection unchecked
                                Iterator<Attribute> it = startElement.getAttributes(); it.hasNext(); ) {
                            Attribute attr = it.next();
                            if ("start".equals(attr.getName().getLocalPart()) || "stop".equals(attr.getName().getLocalPart())) {
                                String date = staxFilter.formatDate(attr.getValue(), xmltvId);
                                attr = eventFactory.createAttribute(attr.getName(), date);
                            }
                            attributeList.add(attr);
                        }
                        event = eventFactory.createStartElement(startElement.getName(), attributeList.iterator(), startElement.getNamespaces());
                    }
                } else {
                    tagName = null;
                }

                writer.add(event);
            }
            writer.close();
            eventReader.close();
        } catch (IOException | XMLStreamException e) {
            log.log(Level.ERROR, e.getMessage(), e);
        }
*/
    }

    class JsonChannel {
        String name;
        String logo;
    }

    @Override
    public void printJson(OutputStream out, final Filter filter, boolean beautify) throws InterruptedException {
/*
        try {
            MyStreamFilter staxFilter = new MyStreamFilter(filter);
            InputStream in = new BufferedInputStream(new GZIPInputStream(new FileInputStream(getXmltvFile())));

            XMLInputFactory inputFactory = XMLInputFactory.newInstance();

            XMLStreamReader reader = inputFactory.createXMLStreamReader(in);
            reader = inputFactory.createFilteredReader(reader, staxFilter);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(reader);

            JsonWriter jsonWriter = new JsonWriter(new BufferedWriter(new OutputStreamWriter(out, "UTF-8")));
            if (beautify) {
                jsonWriter.setIndent("\t");
            }
            jsonWriter.beginObject();
            jsonWriter.name("channels").beginArray();

            Map<String, JsonChannel> channels = new HashMap<String, JsonChannel>();
            String tagName = null, channelId = null, start = null, stop = null, title = null, desc = null, category = null;
            String currentChannelId = null;
            while (reader.hasNext()) {
                final XMLEvent event = eventReader.nextEvent();
                if (event.getEventType() == XMLStreamConstants.CHARACTERS) {
                    if ("title".equals(tagName)) {
                        title = event.asCharacters().getData();
                    } else if ("desc".equals(tagName)) {
                        desc = event.asCharacters().getData();
                    } else if ("category".equals(tagName)) {
                        category = event.asCharacters().getData();
                    }
                } else if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
                    StartElementEvent el = ((StartElementEvent) event);
                    tagName = el.nameAsString();
                    if ("channel".equals(tagName)) {
                        channelId = el.getAttributeByName(new QName("id")).getValue();
                        channels.put(channelId, new JsonChannel());
                    } else if ("programme".equals(tagName)) {
                        StartElement startElement = (StartElement) event;
                        for (//noinspection unchecked
                                Iterator<Attribute> it = startElement.getAttributes(); it.hasNext(); ) {
                            Attribute attr = it.next();
                            String localPart = attr.getName().getLocalPart();
                            if ("start".equals(localPart)) {
                                start = attr.getValue();
                            } else if ("stop".equals(localPart)) {
                                stop = attr.getValue();
                            } else if ("channel".equals(localPart)) {
                                channelId = attr.getValue();
                            }
                        }
                    }
                } else if (event.getEventType() == XMLStreamConstants.END_ELEMENT) {
                    EndElementEvent el = ((EndElementEvent) event);
                    String localPart = el.getName().getLocalPart();
                    if ("programme".equals(localPart)) {
                        if (currentChannelId == null || !currentChannelId.equals(channelId)) {
                            if (currentChannelId != null) {
                                jsonWriter.endArray();
                                jsonWriter.endObject();
                            }
                            JsonChannel jsonChannel = channels.get(channelId);
                            jsonWriter.beginObject();
                            jsonWriter.name("name").value(jsonChannel.name);
                            jsonWriter.name("c").value(channelId);
                            jsonWriter.name("tv").beginArray();
                            currentChannelId = channelId;
                        }
                        jsonWriter.beginObject();
                        jsonWriter.name("t1").value(staxFilter.formatDate(start, channelId));
                        jsonWriter.name("t2").value(staxFilter.formatDate(stop, channelId));
                        jsonWriter.name("t").value(title);
                        if (desc != null) jsonWriter.name("d").value(desc);
                        if (category != null) jsonWriter.name("ctg").value(getCategory(category));
                        title = desc = category = null;
                        jsonWriter.endObject();
                    } else if ("display-name".equals(localPart)) {
                        String name = staxFilter.getChannelNames().get(channelId);
                        if (name != null) {
                            channels.get(channelId).name = name;
                        }
                    }
                    tagName = null;
                } else {
                    tagName = null;
                }

            }
            eventReader.close();

            jsonWriter.endArray().endObject();
            jsonWriter.endArray().name("categories").beginArray();
            for (String cat : categories.keySet()) {
                jsonWriter.value(cat);
            }
            jsonWriter.endArray().endObject().close();
        } catch (IOException e) {
            log.log(Level.ERROR, e.getMessage(), e);
        } catch (XMLStreamException e) {
            log.log(Level.ERROR, e.getMessage(), e);
        }
*/
    }

    private Map<String, Integer> categories = new LinkedHashMap<String, Integer>();

    {
        loadCategories(
                "Спорт",
                "Художественный фильм",
                "Сериал",
                "Познавательные",
                "Развлекательные",
                "Информационные",
                "Детям",
                "Для взрослых");
    }

    private int getCategory(String category) {
        if (!categories.containsKey(category)) {
            categories.put(category, categories.size());
        }
        return categories.get(category);
    }

    private void loadCategories(String... categories) {
        for (String category : categories) {
            getCategory(category);
        }
    }

    private class MyStreamFilter implements StreamFilter {
        private final Map<String, Map<String, String>> filterData;
        private final Filter filter;
        private final TimeFormatter fmt;
        private final Map<String, DbChannel> xmltvChannels;
        private final Map<String, String> channelNames;
        boolean isVisible = true;

        public MyStreamFilter(Filter filter) throws XMLStreamException, InterruptedException, IOException {
            this.filter = filter;

            filterData = new HashMap<String, Map<String, String>>();
            final ConcurrentMap<String, DbChannel> dbChannels = dbService.getChannels();
            channelNames = new HashMap<String, String>();
            xmltvChannels = new HashMap<String, DbChannel>();

/*
            for (ExtInf extInf : tvProgramService.loadChannels().getItems()) {
                String channelId = extInf.get(ExtInf.Attribute.tvg_name);
                DbChannel dbChannel = dbChannels.get(channelId);
                if (dbChannel != null && StringUtils.isNotEmpty(dbChannel.getXmltv())) {
                    Map<String, String> data = new HashMap<String, String>();
                    filterData.put(dbChannel.getXmltv(), data);
                    data.put("enabled", Boolean.toString(dbChannel.isEnabled()));
                    data.put("group", extInf.get(ExtInf.Attribute.group_title));

                    xmltvChannels.put(dbChannel.getXmltv(), dbChannel);

                    channelNames.put(dbChannel.getXmltv(), extInf.getName());
                }
            }

*/
            fmt = new TimeFormatter();
        }

        public Map<String, String> getChannelNames() {
            return channelNames;
        }

        private String formatDate(String date, String xmltvId) {
            DbChannel dbChannel = xmltvChannels.get(xmltvId);
            String result = fmt.format(date);
            if (dbChannel != null && dbChannel.getTimeShift() != 0)
                result = TimeFormatter.formatDateWithShift(result + " " + TimeFormatter.formatTimeShift(dbChannel.getTimeShift()));
            return result;
        }

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
                                map.put("start", formatDate(reader.getAttributeValue(0), xmltvId));
                                map.put("stop", formatDate(reader.getAttributeValue(1), xmltvId));
                                isVisible = filter.accept(map);
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
    }
}
