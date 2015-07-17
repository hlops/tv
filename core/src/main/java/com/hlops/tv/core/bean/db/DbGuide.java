package com.hlops.tv.core.bean.db;

import com.hlops.tv.core.service.Filter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 * User: akarnachuk
 * Date: 6/18/15
 * Time: 11:47 AM
 */
public class DbGuide implements Serializable, Cloneable {
    private static final long serialVersionUID = -6493820826965815964L;

    private String id;
    private String name;
    private String logo;
    private DbTvItem[] items = new DbTvItem[0];

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DbTvItem[] getItems() {
        return items;
    }

    public void setItems(DbTvItem[] items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbGuide)) return false;

        DbGuide dbGuide = (DbGuide) o;

        if (id != null ? !id.equals(dbGuide.id) : dbGuide.id != null) return false;
        if (logo != null ? !logo.equals(dbGuide.logo) : dbGuide.logo != null) return false;
        if (name != null ? !name.equals(dbGuide.name) : dbGuide.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (logo != null ? logo.hashCode() : 0);
        return result;
    }

    public List<DbTvItem> getItems(Filter filter) {
        return Arrays.stream(items).filter(p -> p.applyFilter(filter)).collect(Collectors.toList());
    }

    public boolean applyFilter(Filter filter) {
        Map<String, String> map = new HashMap<>();
        if (id != null) map.put("guideId", id);
        if (name != null) map.put("guideName", name);

        return filter.accept(map);
    }

}
