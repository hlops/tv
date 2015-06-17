package com.hlops.tv.core.bean;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class M3UTest extends Assert {
    @Test
    public void loadPlaylist() throws IOException {
        URL url = getClass().getClassLoader().getResource("playlist.m3u");
        //noinspection ConstantConditions
        M3U m3u = new M3U(new FileInputStream(new File(url.getFile())), StandardCharsets.UTF_8);
        assertEquals("http://www.cn.ru/data/tv/schedule.zip", m3u.get(M3U.Attribute.url_tvg));
        assertEquals("1", m3u.get(M3U.Attribute.m3uautoload));
        assertEquals("500", m3u.get(M3U.Attribute.cache));
        assertEquals("1", m3u.get(M3U.Attribute.deinterlace));
        assertEquals("4:3", m3u.get(M3U.Attribute.aspect_ratio));
        assertEquals("690x550+10+10", m3u.get(M3U.Attribute.crop));
        assertEquals("http://iptv.cn.ru/iptv-stat/post.php", m3u.get(M3U.Attribute.reportstat));
        assertEquals("http://clr.novotelecom.ru/iptv/log.php", m3u.get(M3U.Attribute.reportlog));

        {
            ExtInf[] items = m3u.findByTvgName("2");
            assertEquals(1, items.length);
            assertEquals("-1", items[0].getDuration());
            assertEquals("Первый", items[0].getName());
            assertEquals("udp://@239.1.15.1:1234", items[0].getUrl());
            assertEquals("2", items[0].get(ExtInf.Attribute.tvg_name));
            assertEquals("4:3", items[0].get(ExtInf.Attribute.aspect_ratio));
            assertEquals("Эфир", items[0].get(ExtInf.Attribute.group_title));
        }

        {
            ExtInf[] items = m3u.findByTvgName("81003");
            assertEquals(1, items.length);
            assertEquals("-1", items[0].getDuration());
            assertEquals("Amedia Premium HD", items[0].getName());
            assertEquals("udp://@239.1.17.29:1234", items[0].getUrl());
            assertEquals("81003", items[0].get(ExtInf.Attribute.tvg_name));
            assertEquals("16:9", items[0].get(ExtInf.Attribute.aspect_ratio));
            assertEquals("1920x1080+0+0", items[0].get(ExtInf.Attribute.crop));
            assertEquals("Клуб HD", items[0].get(ExtInf.Attribute.group_title));
        }

        {
            ExtInf[] items = m3u.findByGroup("Познавательный");
            assertEquals(21, items.length);
        }

    }

    @Test
    public void parseLine() throws IOException {
        HashMap<String, String> expected = new HashMap<String, String>();
        expected.put("aaaa", "111");
        expected.put("b", "2 2");
        expected.put("c", "3 3 ");
        expected.put("d", "\"=4");
        expected.put("e", "");
        assertEquals(expected, M3U.parseLine("aaaa=111   b='2 2'\tc=\"3 3 \" \r\nd='\"=4' e=''"));
    }

}