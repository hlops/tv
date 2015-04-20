package com.hlops.tv.core.service.impl;

import com.hlops.tv.core.service.FilterOperation;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Created by tom on 4/12/15.
 */
public class FilterOperationImpl implements FilterOperation {

    interface IOperation {
        boolean equals(String s1, String s2);
    }

    enum Operation implements IOperation {
        eq {
            @Override
            public boolean equals(String s1, String s2) {
                return StringUtils.equalsIgnoreCase(s1, s2);
            }
        },

        lt {
            @Override
            public boolean equals(String s1, String s2) {
                return compare(s1, s2) > 0;
            }
        };

        private static int compare(String s1, String s2) {
            if (s1 == null && s2 == null) return 0;
            return 0;
        }

    }

    private final String key;
    private final String[] values;

    public FilterOperationImpl(String key, String[] values) {
        this.key = key;
        this.values = values == null ? new String[0] : values;
    }

    @Override
    public boolean check(Map<String, String> map) {
        boolean result = true;
        for (String value : values) {
            if (map.containsKey(key)) {
                if (StringUtils.equalsIgnoreCase(map.get(key), value)) {
                    return true;
                }
                result = false;
            }
        }
        return result;
    }
}
