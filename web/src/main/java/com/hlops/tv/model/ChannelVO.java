package com.hlops.tv.model;

import com.hlops.tv.core.bean.ExtInf;
import com.hlops.tv.core.bean.db.DbChannel;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by tom on 4/4/15.
 */
@XmlRootElement
public class ChannelVO {

    private boolean enabled;
    private String name;
    private String tvgName;
    private String group;
    private String aspect;
    private String crop;

    public ChannelVO() {
    }

    public ChannelVO(ExtInf extInf, DbChannel dbChannel) {
        setEnabled(dbChannel.isEnabled());
        setName(extInf.getName());
        setTvgName(extInf.get(ExtInf.Attribute.tvg_name));
        setAspect(extInf.get(ExtInf.Attribute.aspect_ratio));
        setCrop(extInf.get(ExtInf.Attribute.crop));
        setGroup(extInf.get(ExtInf.Attribute.group_title));
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTvgName() {
        return tvgName;
    }

    public void setTvgName(String tvgName) {
        this.tvgName = tvgName;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
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
}
