package com.hlops.tv.core.service.impl.filter;

import com.hlops.tv.core.service.FilterOperation;

import java.util.Map;

/**
 * Created by tom on 4/12/15.
 */
public class FilterOperationImpl implements FilterOperation {

    private final String key;
    private final String[] values;
    private Operation operation = Operation.eq;
    private Formatter fmt = new StringFormatter();

    public FilterOperationImpl(String key, String[] values) {
        this.key = key;
        this.values = values == null ? new String[0] : values;
    }

    public void setOperation(String operationName) {
        operation = Operation.valueOf(operationName);
    }

    public void setFormat(String name) {
        if ("time".equals(name)) {
            fmt = new TimeFormatter();
        } else {
            fmt = new StringFormatter();
        }
    }

    @Override
    public boolean check(Map<String, String> map) {
        boolean result = true;
        for (String value : values) {
            if (map.containsKey(key)) {
                try {
                    if (operation.success(map.get(key), fmt.format(value))) {
                        return true;
                    }
                } catch (NullPointerException e) {
                    // ignore
                }
                result = false;
            }
        }
        return result;
    }

}
