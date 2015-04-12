package com.hlops.tv.core.service;

import java.util.Map;

/**
 * Created by tom on 4/12/15.
 */
public interface FilterOperation {

    boolean check(Map<String, String> values);
}
