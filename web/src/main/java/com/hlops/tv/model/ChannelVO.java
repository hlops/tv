package com.hlops.tv.model;

import com.hlops.tv.core.bean.db.DbChannel;
import com.hlops.tv.core.bean.db.DbGuide;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by tom on 4/4/15.
 */
@XmlRootElement
public class ChannelVO {

    private String id;
    private Boolean enabled;
    private String name;
    private String group;
    private String aspect;
    private String crop;
    private String guideId;
    private int timeShift;

    public ChannelVO(DbChannel channel) {
        this.setId(channel.getUrl());
        this.setEnabled(channel.isEnabled());
        this.setName(channel.getTvgName());
        this.setGroup(channel.getGroup());
        //this.setAspect();
        this.setTimeShift(channel.getTimeShift());
        this.setGuideId(channel.getGuideId());
    }

    public String getAspect() {
        return aspect;
    }

    public void setAspect(String aspect) {
        this.aspect = aspect;
    }

    public String getCrop() {
        return crop;
    }

    public void setCrop(String crop) {
        this.crop = crop;
    }

    public Boolean getEnabled() {
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTimeShift() {
        return timeShift;
    }

    public void setTimeShift(int timeShift) {
        this.timeShift = timeShift;
    }

    public String getGuideId() {
        return guideId;
    }

    public void setGuideId(String guideId) {
        this.guideId = guideId;
    }
}
