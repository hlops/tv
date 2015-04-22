package com.hlops.tv.core.service.impl.filter;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tom on 4/21/15.
 */
class TimeFormatter extends Formatter {

    private static Logger log = LogManager.getLogger(TimeFormatter.class);

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

    private static final Pattern TIME_PATTERN = Pattern.compile("([+-]?)(\\d+)(\\s[+-]\\d{4})?([smhd]?)");

    private long currentTime = System.currentTimeMillis();

    @Override
    String format(String s) {
        if (StringUtils.isNotEmpty(s)) {
            Matcher m = TIME_PATTERN.matcher(s);
            if (m.matches()) {
                if ("".equals(m.group(1)) && "".equals(m.group(4))) {
                    if (m.group(2).length() == 14) {
                        try {
                            Date d = DATE_FORMAT.parse(m.group(2));
                            if (StringUtils.isNotEmpty(m.group(3))) {
                                int shift = Integer.parseInt(m.group(3).substring(2, 4)) * 60 + Integer.parseInt(m.group(3).substring(4));
                                if ("-".equals(m.group(3).substring(1, 2))) {
                                    shift = -shift;
                                }
                                return DATE_FORMAT.format(new Date(d.getTime() + 60000 * shift));
                            }
                        } catch (ParseException e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                    return m.group(2);
                }
                if ("+".equals(m.group(1))) {
                    return DATE_FORMAT.format(new Date(currentTime + getInterval(m.group(2), m.group(4))));
                }
                if ("-".equals(m.group(1))) {
                    return DATE_FORMAT.format(new Date(currentTime - getInterval(m.group(2), m.group(4))));
                }
            }
        }
        return s;
    }

    private long getInterval(String value, String unit) {
        long n = Integer.valueOf(value);
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

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }
}
