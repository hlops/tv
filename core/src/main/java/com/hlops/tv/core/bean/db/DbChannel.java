package com.hlops.tv.core.bean.db;

import com.hlops.tv.core.bean.ExtInf;

import java.io.Serializable;

/**
 * Created by tom on 4/1/15.
 */
public class DbChannel implements Serializable {

    private boolean enabled = true;
    private String xmltv;
    private String group;
    private short timeShift;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getXmltv() {
        return xmltv;
    }

    public void setXmltv(String xmltv) {
        this.xmltv = xmltv;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public short getTimeShift() {
        return timeShift;
    }

    public void setTimeShift(short timeShift) {
        this.timeShift = timeShift;
    }

    public void parse(ExtInf item) {
    }

}
