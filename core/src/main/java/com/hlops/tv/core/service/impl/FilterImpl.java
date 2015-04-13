package com.hlops.tv.core.service.impl;

import com.hlops.tv.core.service.Filter;
import com.hlops.tv.core.service.FilterOperation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by tom on 4/12/15.
 */
public class FilterImpl implements Filter {

    private final List<FilterOperation> operations;

    FilterImpl(FilterOperation... operations) {
        this.operations = new ArrayList<FilterOperation>(Arrays.asList(operations));
    }

    @Override
    public boolean accept(Map<String, String> values) {
        for (FilterOperation op : operations) {
            if (!op.check(values)) {
                return false;
            }
        }
        return true;
    }
}
