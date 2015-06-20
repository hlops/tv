package com.hlops.tv.core.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import com.hlops.tv.core.bean.db.DbChannel;
import com.hlops.tv.core.bean.db.DbGuide;
import com.hlops.tv.core.service.Filter;
import com.hlops.tv.core.service.MapDBService;
import com.hlops.tv.core.service.XmltvService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
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

    private File xmltvFile;
    private boolean programBindingDirty;

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

}
