package com.hlops.tv.core.bean.db;

import java.io.Serializable;

/**
 * Created by tom on 4/1/15.
 */
public class DbChannel implements Serializable, Cloneable {
    private static final long serialVersionUID = 3934814021150382405L;

    enum Attribute {
        wide, hd
    }

    private String name;
    private String url;
    private Boolean enabled;
    private String group;
    private String tvgId;
    private String tvgName;
    private int timeShift;
    private Attribute[] attributes;
    private DbTvItem[] items;

    public DbChannel() {
    }

    public Attribute[] getAttributes() {
        return attributes;
    }

    public void setAttributes(Attribute[] attributes) {
        this.attributes = attributes;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
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

    public DbTvItem[] getItems() {
        return items;
    }

    public void setItems(DbTvItem[] items) {
        this.items = items;
    }

    @SuppressWarnings("CloneDoesntDeclareCloneNotSupportedException")
    @Override
    public DbChannel clone() {
        DbChannel clone = null;
        try {
            clone = (DbChannel) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return clone;
    }
}
