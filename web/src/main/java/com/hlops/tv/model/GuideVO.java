package com.hlops.tv.model;

import com.hlops.tv.core.bean.db.DbGuide;

/**
 * Created by tom on 7/21/15.
 */
public class GuideVO {

    private String id;
    private String name;

    public GuideVO(DbGuide dbGuide) {
        this.id = dbGuide.getId();
        this.name = dbGuide.getName();
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
}
