package com.hlops.tv.core.service.impl;

import com.hlops.tv.core.bean.ExtInf;
import com.hlops.tv.core.service.Filter;
import com.hlops.tv.core.service.FilterOperation;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
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
        String[] split = name.split("\\.", 2);
        FilterOperation op = null;
        if (split.length >= 2) {
            if ("eq".equals(split[1])) {
                op = new FilterOperationImpl(split[0], values);
            } else if ("eq".equals(split[1])) {
                op = new (split[0], values);
            }

            if (op == null) {
                op = new FilterOperationImpl(split[0], values);
            }

            return op;
        }

        public Map<String, String> prepare (ExtInf item, DbChannel dbChannel){
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("enabled", Boolean.toString(dbChannel.isEnabled()));
            map.put("xmltv", dbChannel.getXmltv());
            map.put("channel", item.getName());
            map.put("group", item.get(ExtInf.Attribute.group_title));
            return map;
        }
    }
