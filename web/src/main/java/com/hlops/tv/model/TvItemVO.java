package com.hlops.tv.model;

import com.google.gson.annotations.SerializedName;
import com.hlops.tv.core.bean.db.DbTvItem;

/**
 * Created by IntelliJ IDEA.
 * User: akarnachuk
 * Date: 7/17/15
 * Time: 6:19 PM
 */
public class TvItemVO {

    @SerializedName("t")
    private String title;
    @SerializedName("d")
    private String description;
    @SerializedName("c")
    private String category;
    @SerializedName("t1")
    private String start;
    @SerializedName("t2")
    private String stop;

    public TvItemVO(DbTvItem item) {
        this.title = item.getTitle();
        this.description = item.getDescription();
        this.category = item.getCategory();
        this.start = item.getStart();
        this.stop = item.getStop();
    }

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
}
