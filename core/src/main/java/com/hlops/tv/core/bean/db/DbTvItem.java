package com.hlops.tv.core.bean.db;

import com.hlops.tv.core.service.Filter;
import org.apache.commons.beanutils.BeanUtilsBean;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

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

    public boolean applyFilter(Filter filter) {
        try {
            //noinspection unchecked
            Map<String, String> map = BeanUtilsBean.getInstance().describe(this);
            return filter.accept(map);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return false;
    }
}
