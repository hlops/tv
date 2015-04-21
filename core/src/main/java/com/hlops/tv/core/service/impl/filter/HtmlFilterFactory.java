package com.hlops.tv.core.service.impl.filter;

import com.hlops.tv.core.service.Filter;
import com.hlops.tv.core.service.FilterOperation;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by tom on 4/12/15.
 */

@Component
public class HtmlFilterFactory {

    public Filter createFilter(Map<String, String[]> requestMap) {
        List<FilterOperation> operations = new ArrayList<FilterOperation>();
        for (Map.Entry<String, String[]> entry : requestMap.entrySet()) {
            operations.add(createOperation(entry.getKey(), entry.getValue()));
        }
        return new FilterImpl(operations.toArray(new FilterOperation[operations.size()]));
    }

    private FilterOperation createOperation(String name, String[] values) {
        String[] split = name.split("\\.", 3);
        FilterOperationImpl op = new FilterOperationImpl(split[0], values);
        if (split.length >= 2) {
            op.setOperation(split[1]);
        }
        if (split.length >= 3) {
            op.setFormat(split[2]);
        }

        return op;
    }
}
