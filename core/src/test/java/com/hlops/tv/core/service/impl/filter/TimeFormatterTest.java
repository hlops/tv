package com.hlops.tv.core.service.impl.filter;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;

public class TimeFormatterTest extends Assert {

    @Test
    public void testFormat() throws Exception {
        assertEquals("1", new TimeFormatter().format("1"));
        assertEquals("1m", new TimeFormatter().format("1m"));

        assertEquals("20150329084000", new TimeFormatter().format("20150329084000"));
        assertEquals("20150329114000", new TimeFormatter().format("20150329084000 +0300"));
        assertEquals("20150329083500", new TimeFormatter().format("20150329084000 -0005"));

        {
            TimeFormatter timeFormatter = new TimeFormatter();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timeFormatter.getCurrentTime());
            calendar.add(Calendar.SECOND, 1);
            assertEquals(TimeFormatter.DATE_FORMAT.format(calendar.getTime()), timeFormatter.format("+1s"));
        }

        {
            TimeFormatter timeFormatter = new TimeFormatter();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timeFormatter.getCurrentTime());
            calendar.add(Calendar.MINUTE, -200);
            assertEquals(TimeFormatter.DATE_FORMAT.format(calendar.getTime()), timeFormatter.format("-200m"));
        }

        {
            TimeFormatter timeFormatter = new TimeFormatter();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timeFormatter.getCurrentTime());
            calendar.add(Calendar.HOUR, 15);
            assertEquals(TimeFormatter.DATE_FORMAT.format(calendar.getTime()), timeFormatter.format("+15h"));
            assertEquals(TimeFormatter.DATE_FORMAT.format(calendar.getTime()), timeFormatter.format("+15"));
        }

        {
            TimeFormatter timeFormatter = new TimeFormatter();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timeFormatter.getCurrentTime());
            calendar.add(Calendar.DAY_OF_YEAR, 199);
            assertEquals(TimeFormatter.DATE_FORMAT.format(calendar.getTime()), timeFormatter.format("+199d"));
        }
    }
}