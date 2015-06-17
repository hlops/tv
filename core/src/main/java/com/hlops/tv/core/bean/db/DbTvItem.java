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

    private String title;
    private String description;
    private String category;
    private String start;
    private String stop;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getStop() {
        return stop;
    }

    public void setStop(String stop) {
        this.stop = stop;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbTvItem)) return false;

        DbTvItem item = (DbTvItem) o;

        if (start != null ? !start.equals(item.start) : item.start != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return start != null ? start.hashCode() : 0;
    }
}