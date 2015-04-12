package com.hlops.tv.core.bean.db;

import com.hlops.tv.core.bean.ExtInf;

import java.io.Serializable;

/**
 * Created by tom on 4/1/15.
 */
public class DbChannel implements Serializable {

    public static final String NAME = "dbChannel";

    private boolean enabled = true;
    private String xmltv;

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

    public void parse(ExtInf item) {
    }

}
