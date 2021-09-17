package com.y3tu.tools.kit.base;

import org.junit.Test;

import java.net.URL;

import static org.junit.Assert.*;

public class ResourceUtilTest {

    @Test
    public void getResource() {
       URL url = ResourceUtil.getResource("com",null);
       System.out.println(url);
    }
}