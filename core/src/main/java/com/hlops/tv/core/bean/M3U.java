package com.hlops.tv.core.bean;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
public class M3U implements Serializable {

    private static final long serialVersionUID = -692564003554169230L;

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

    private final Map<String, String> attrs = new LinkedHashMap<String, String>();
    private final List<ExtInf> items = new ArrayList<ExtInf>();

    public M3U(InputStream is, Charset cs) throws IOException {
        this(new InputStreamReader(is, cs));
    }

    public M3U(InputStreamReader reader) throws IOException {
        this(new BufferedReader(reader));
    }

    public M3U(BufferedReader sr) throws IOException {
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
        Map<String, String> map = new LinkedHashMap<String, String>();
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

    public Map<String, String> getAttributes() {
        return attrs;
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
