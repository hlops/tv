package com.hlops.tv.core.service.impl;

import com.hlops.tv.core.service.MapDBService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring-config.xml"})
public class TVProgramServiceImplTest extends Assert {

    @Autowired
    private TVProgramServiceImpl tvProgramService;

    @Autowired
    private MapDBService dbService;

    @Test
    public void loadChannels() throws Exception {
        //dbService.drop();
        tvProgramService.loadChannels();
    }

}