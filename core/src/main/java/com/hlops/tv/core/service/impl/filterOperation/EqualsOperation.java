package com.hlops.tv.core.service.impl.filterOperation;

import com.hlops.tv.core.service.FilterOperation;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Created by tom on 4/12/15.
 */
public class EqualsOperation implements FilterOperation {

    private final String key;
    private final String[] values;

    public EqualsOperation(String key, String[] values) {
        this.key = key;
        this.values = values == null ? new String[0] : values;
    }

    @Override
    public boolean check(Map<String, String> map) {
        boolean result = true;
        for (String value : values) {
            if (map.containsKey(key)) {
                if (StringUtils.equalsIgnoreCase(value, map.get(key))) {
                    return true;
                }
                result = false;
            }
        }
        return result;
    }
}
