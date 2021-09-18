package com.y3tu.tools.kit.io;

import org.junit.Test;

import java.net.URL;

public class ResourceUtilTest {

    @Test
    public void getResource() {

        URL url = ResourceUtil.getResource("ip2region",null);
        System.out.println(url);
    }
}