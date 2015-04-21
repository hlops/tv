package com.hlops.tv.core.service.impl.filter;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tom on 4/21/15.
 */
class TimeFormatter extends Formatter {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

    private static final Pattern TIME_PATTERN = Pattern.compile("([+-]?)(\\d+)(\\s[+-]\\d*)?([smhd]?)");

    @Override
    String format(String s) {
        if (StringUtils.isNotEmpty(s)) {
            Matcher m = TIME_PATTERN.matcher(s);
            if (m.matches()) {
                if ("".equals(m.group(1)) && "".equals(m.group(4))) {
                    return m.group(2);
                }
                if ("+".equals(m.group(1))) {
                    return DATE_FORMAT.format(new Date(System.currentTimeMillis() + getInterval(m.group(2), m.group(4))));
                }
                if ("-".equals(m.group(1))) {
                    return DATE_FORMAT.format(new Date(System.currentTimeMillis() - getInterval(m.group(2), m.group(4))));
                }
            } else {
                System.out.println("not matched");
            }
        }
        return s;
    }

    private long getInterval(String value, String unit) {
        int n = Integer.valueOf(value);
        //noinspection StatementWithEmptyBody
        if ("s".equals(unit)) {
            // seconds
        } else if ("m".equals(unit)) {
            n = n * 60;
        } else if ("d".equals(unit)) {
            n = n * 60 * 60 * 24;
        } else {
            // hours - default
            n = n * 60 * 60;
        }
        return n * 1000;
    }

}
