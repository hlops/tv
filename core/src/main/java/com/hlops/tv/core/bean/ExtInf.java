package com.hlops.tv.core.bean;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: akarnachuk
 * Date: 7/31/14
 * Time: 2:22 PM
 */
public class ExtInf implements Serializable {

    private final static Pattern attributePattern = Pattern.compile("(([A-z][A-z0-9\\-_\\:]*)=(\".*?\"|'.*?'|\\S*)\\s*)");
    private final static Pattern timeShiftPattern = Pattern.compile("\\#EXTINF\\:([0-9\\-]+)\\s+(.*),\\s*(.*)");

    public enum Attribute {
        tvg_name("tvg-name"),
        aspect_ratio("aspect-ratio"),
        group_title("group-title"),
        crop;

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

    private String duration;
    private String name;
    private String url;

    private final Map<String, String> attrs = new LinkedHashMap<String, String>();

    public ExtInf(String line) {
        Matcher matcher = timeShiftPattern.matcher(line);
        if (matcher.find()) {
            duration = matcher.group(1);
            name = matcher.group(3);
            attrs.putAll(M3U.parseLine(matcher.group(2)));
        }
    }

    Map<String, String> getAttrs() {
        return attrs;
    }

    public static Pattern getAttributePattern() {
        return attributePattern;
    }

    public static Pattern getTimeShiftPattern() {
        return timeShiftPattern;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String get(Attribute attr) {
        return attrs.get(attr.getAttributeName());
    }

    public String set(Attribute attr, String value) {
        return attrs.put(attr.getAttributeName(), value);
    }

}
