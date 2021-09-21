package com.y3tu.tools.web.util;

import org.junit.Assert;
import org.junit.Test;


public class TranslatorUtilTest {

    @Test
    public void translate() {
        Assert.assertEquals("你好", TranslatorUtil.translate("hello"));
        Assert.assertEquals("Hello", TranslatorUtil.translate("你好"));
    }
}