package com.hlops.tv.core.service;

import com.hlops.tv.core.bean.ExtInf;
import com.hlops.tv.core.bean.db.DbChannel;

import java.util.Map;

/**
 * Created by tom on 4/12/15.
 */
public interface Filter {

    boolean accept(Map<String, String> values);
}
