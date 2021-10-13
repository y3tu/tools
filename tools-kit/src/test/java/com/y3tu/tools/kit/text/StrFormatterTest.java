package com.y3tu.tools.kit.text;

import org.junit.Assert;
import org.junit.Test;


public class StrFormatterTest {

    @Test
    public void format() {
        String resultStr = StrFormatter.format("abc{},{}", 1, 2);
        Assert.assertEquals("abc1,2", resultStr);
    }
}