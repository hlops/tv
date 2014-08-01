package com.hlops.tv.core.service.impl;

import com.hlops.tv.core.bean.M3U;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring-config.xml"})
public class TVProgramServiceImplTest {

    @Autowired
    private TVProgramServiceImpl tvProgramService;

    @Test
    public void loadTV() throws Exception {
        M3U m3U = tvProgramService.loadTV();
    }
}