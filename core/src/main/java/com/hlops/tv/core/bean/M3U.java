package com.hlops.tv.core.bean;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: akarnachuk
 * Date: 7/31/14
 * Time: 2:18 PM
 */
public class M3U {

    private final static Pattern attributePattern = Pattern.compile("(([A-z][A-z0-9\\-_\\:]*)=(\".*?\"|'.*?'|\\S*)\\s*)");

    public enum Attribute {
        url_tvg("url-tvg"),
        m3uautoload,
        cache,
        deinterlace,
        aspect_ratio("aspect-ratio"),
        crop,
        reportstat,
        reportlog;

        private final String attributeName;

        Attribute() {
            attributeName = name();
        }

        Attribute(String attributeName) {
            this.attributeName = attributeName;
        }

        public String getAttributeName() {
            return attributeName;
        }
    }

    private final Map<String, String> attrs = new HashMap<String, String>();
    private final List<ExtInf> items = new ArrayList<ExtInf>();

    public M3U(InputStream is, Charset cs) throws IOException {
        BufferedReader sr = new BufferedReader(new InputStreamReader(is, cs));
        try {
            String line, lineUC;
            ExtInf extInf = null;
            String groupTitle = null;
            while ((line = sr.readLine()) != null) {
                if (StringUtils.isBlank(line)) {
                    continue;
                }

                lineUC = line.toUpperCase();

                if (lineUC.startsWith("#EXTM3U")) {
                    attrs.putAll(parseLine(line.substring(8)));
                } else if (lineUC.startsWith("#EXTINF")) {
                    extInf = new ExtInf(line);
                    items.add(extInf);
                    if (extInf.get(ExtInf.Attribute.group_title) == null) {
                        extInf.set(ExtInf.Attribute.group_title, groupTitle);
                    } else {
                        groupTitle = extInf.get(ExtInf.Attribute.group_title);
                    }
                } else {
                    if (extInf != null) {
                        extInf.setUrl(line.trim());
                    }
                }
            }
        } finally {
            sr.close();
        }
    }

    public static Map<String, String> parseLine(String line) {
        Map<String, String> map = new HashMap<String, String>();
        Matcher matcher = attributePattern.matcher(line);
        while (matcher.find()) {
            String name = matcher.group(2);
            String value = matcher.group(3);
            if (value.startsWith("\'") || value.startsWith("\"")) {
                value = value.substring(1, value.length() - 1);
            }
            map.put(name, value);
        }
        return map;
    }

    public String get(Attribute attr) {
        return attrs.get(attr.getAttributeName());
    }

    public String set(Attribute attr, String value) {
        return attrs.put(attr.getAttributeName(), value);
    }

    public ExtInf[] getItems() {
        return items.toArray(new ExtInf[items.size()]);
    }

    public ExtInf[] findByTvgName(String tvgName) {
        List<ExtInf> result = new ArrayList<ExtInf>();
        for (ExtInf item : items) {
            if (tvgName.equals(item.get(ExtInf.Attribute.tvg_name))) {
                result.add(item);
            }
        }
        return result.toArray(new ExtInf[result.size()]);
    }

    public ExtInf[] findByName(String name) {
        List<ExtInf> result = new ArrayList<ExtInf>();
        for (ExtInf item : items) {
            if (name.equals(item.getName())) {
                result.add(item);
            }
        }
        return result.toArray(new ExtInf[result.size()]);
    }

    public ExtInf[] findByGroup(String group) {
        List<ExtInf> result = new ArrayList<ExtInf>();
        for (ExtInf item : items) {
            if (group.equals(item.get(ExtInf.Attribute.group_title))) {
                result.add(item);
            }
        }
        return result.toArray(new ExtInf[result.size()]);
    }
}
