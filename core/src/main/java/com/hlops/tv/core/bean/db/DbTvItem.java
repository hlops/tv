package com.hlops.tv.core.bean.db;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: akarnachuk
 * Date: 6/10/15
 * Time: 7:22 PM
 */
public class DbTvItem implements Serializable {
    private static final long serialVersionUID = 9072000015272751417L;

    private String name;
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
