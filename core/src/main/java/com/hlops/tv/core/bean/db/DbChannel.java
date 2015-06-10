package com.hlops.tv.core.bean.db;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tom on 4/1/15.
 */
public class DbChannel implements Serializable {
    private static final long serialVersionUID = 3934814021150382405L;

    enum Attribute {
        wide, hd
    }

    private String name;
    private String url;
    private boolean enabled;
    private String group;
    private String tvgId;
    private String tvgName;
    private int timeShift;
    private Attribute[] attributes;
    private Map<String, DbTvItem> item = new HashMap<String, DbTvItem>();

    public DbChannel() {
    }

    public Attribute[] getAttributes() {
        return attributes;
    }

    public void setAttributes(Attribute[] attributes) {
        this.attributes = attributes;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getTimeShift() {
        return timeShift;
    }

    public void setTimeShift(int timeShift) {
        this.timeShift = timeShift;
    }

    public String getTvgId() {
        return tvgId;
    }

    public void setTvgId(String tvgId) {
        this.tvgId = tvgId;
    }

    public String getTvgName() {
        return tvgName;
    }

    public void setTvgName(String tvgName) {
        this.tvgName = tvgName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
