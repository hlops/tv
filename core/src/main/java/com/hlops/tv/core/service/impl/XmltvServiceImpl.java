package com.hlops.tv.core.service.impl;

import com.hlops.tv.core.bean.db.DbChannel;
import com.hlops.tv.core.bean.db.DbGuide;
import com.hlops.tv.core.bean.db.DbTvItem;
import com.hlops.tv.core.service.Filter;
import com.hlops.tv.core.service.MapDBService;
import com.hlops.tv.core.service.TVProgramService;
import com.hlops.tv.core.service.XmltvService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

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

    @Autowired
    private TVProgramService tvProgramService;

    private File xmltvFile;
    private boolean programBindingDirty = true;

    @PostConstruct
    private void init() {
        xmltvFile = new File(xmltvFileName);
    }

    public File getFile() {
        return xmltvFile;
    }

    public String getUrl() {
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
    public Map<String, String> getAllChannels() throws InterruptedException {
        Map<String, String> channels = new LinkedHashMap<>();
        for (DbGuide guide : dbService.getGuideChannels().values()) {
            channels.put(guide.getId(), guide.getName());
        }
        return channels;
    }

    @Override
    public void printXmltv(OutputStream out, final Filter filter) throws InterruptedException {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        try {
            XMLStreamWriter writer = factory.createXMLStreamWriter(out);
            writer.writeStartDocument();
            writer.writeDTD("<!DOCTYPE tv SYSTEM \"http://www.teleguide.info/xmltv.dtd\">");
            writer.writeCharacters("\n");
            writer.writeStartElement("tv");
            writer.writeCharacters("\n");

            DbChannel[] channels = tvProgramService.getChannels(filter);
            for (DbChannel channel : channels) {
                if (channel.getGuideId() != null) {
                    writer.writeStartElement("channel");
                    writer.writeAttribute("id", channel.getGuideId());

                    writer.writeStartElement("display-name");
                    writer.writeCharacters(channel.getTvgName());
                    writer.writeEndElement();

                    writer.writeEndElement();
                    writer.writeCharacters("\n");
                }
            }

            for (DbChannel channel : channels) {
                if (channel.getGuideId() != null) {
                    DbGuide dbGuide = tvProgramService.getDbGuide(channel.getGuideId());
                    if (dbGuide != null) {
                        for (DbTvItem item : dbGuide.getItems(filter)) {
                            writer.writeStartElement("programme");
                            writer.writeAttribute("start", item.getStart());
                            writer.writeAttribute("stop", item.getStop());
                            writer.writeAttribute("channel", channel.getGuideId());

                            writer.writeStartElement("title");
                            writer.writeCharacters(item.getTitle());
                            writer.writeEndElement();

                            if (item.getDescription() != null) {
                                writer.writeStartElement("desc");
                                writer.writeCharacters(item.getDescription());
                                writer.writeEndElement();
                            }

                            if (item.getCategory() != null) {
                                writer.writeStartElement("category");
                                writer.writeCharacters(item.getCategory());
                                writer.writeEndElement();
                            }

                            writer.writeEndElement();
                            writer.writeCharacters("\n");
                        }
                    }
                }
            }

            writer.writeEndDocument();

            writer.flush();
            writer.close();

        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

}
