package com.y3tu.tools.kit.text;

import org.junit.Assert;
import org.junit.Test;

public class StrUtilTest {

    /**
     * 指定范围内查找字符串
     */
    @Test
    public void indexOf() {
        String testStr = "abcdefg";
        int index = StrUtil.indexOf(testStr, "Cde", 0, true);
        Assert.assertEquals(2, index);
        index = StrUtil.indexOf(testStr, "cde", 0, false);
        Assert.assertEquals(2, index);
    }

    /**
     * 替换字符串中的指定字符串
     */
    @Test
    public void replace() {
        String testStr = "abcdefg";
        String resultStr = StrUtil.replace(testStr, 0, "cde", "xxx", false);
        Assert.assertEquals("abxxxfg", resultStr);

        resultStr = StrUtil.replace(testStr, 0, "CDe", "xxx", true);
        Assert.assertEquals("abxxxfg", resultStr);

    }
}