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
public class TimeFormatter extends Formatter {

    private static Logger log = LogManager.getLogger(TimeFormatter.class);

    public static final String DATE_FORMAT_PATTERN = "yyyyMMddHHmmss";
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_PATTERN);

    private static final Pattern TIME_PATTERN = Pattern.compile("([+-]?)([0-9 \\-+]+)([smhd]?)");

    private long currentTime = System.currentTimeMillis();

    public static String formatDateWithShift(String date) {
        if (date.length() == 20 && date.charAt(14) == ' ') {
            try {
                Date d = DATE_FORMAT.parse(date.substring(0, 15));
                String shift = date.substring(15);
                int minutes = Integer.parseInt(shift.substring(1, 3)) * 60 + Integer.parseInt(shift.substring(3));
                if (shift.charAt(0) == '-') {
                    minutes = -minutes;
                }
                return DATE_FORMAT.format(new Date(d.getTime() + 60000 * minutes));
            } catch (ParseException e) {
                log.error(e.getMessage(), e);
            }
        }
        return date;
    }

    @Override
    public String format(String s) {
        if (StringUtils.isNotEmpty(s)) {
            Matcher m = TIME_PATTERN.matcher(s);
            if (m.matches()) {
                if ("".equals(m.group(1)) && "".equals(m.group(3))) {
                    return formatDateWithShift(m.group(2));
                }
                if ("+".equals(m.group(1))) {
                    return DATE_FORMAT.format(new Date(currentTime + getInterval(m.group(2), m.group(3))));
                }
                if ("-".equals(m.group(1))) {
                    return DATE_FORMAT.format(new Date(currentTime - getInterval(m.group(2), m.group(3))));
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

    public static String formatTimeShift(int timeShift) {
        StringBuilder sb = new StringBuilder("0");
        sb.append(Math.abs(timeShift)).append("00");
        return (timeShift < 0 ? "-" : "+") + sb.substring(sb.length() - 4);
    }

}
