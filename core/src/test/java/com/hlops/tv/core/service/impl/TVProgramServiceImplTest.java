package com.hlops.tv.core.service.impl;

import com.hlops.tv.core.bean.db.DbGuide;
import com.hlops.tv.core.service.Filter;
import com.hlops.tv.core.service.MapDBService;
import com.hlops.tv.core.service.XmltvService;
import com.hlops.tv.core.service.impl.filter.FilterImpl;
import com.hlops.tv.core.service.impl.filter.HtmlFilterFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring-config.xml"})
public class TVProgramServiceImplTest extends Assert {

    @Autowired
    private TVProgramServiceImpl tvProgramService;

    @Autowired
    private XmltvService xmltvService;

    @Autowired
    private MapDBService dbService;

    @Test
    public void loadChannels() throws Exception {
        //dbService.drop();
        //xmltvService.setProgramBindingDirty(true);
        tvProgramService.loadChannels();

/*
        Map<String, String[]> map = new HashMap<>();
        Filter filter = new HtmlFilterFactory().createFilter(map);
        DbGuide[] guideChannels = tvProgramService.getGuideChannels(filter);
        System.out.println(guideChannels);
*/
    }

}