package com.y3tu.tools.web.cache;

import org.junit.Assert;
import org.junit.Test;


public class CacheUtilTest {
    @Test
    public void localCache() {
        CacheUtil.localCache("test").put("nihao", "wahaha");
        Assert.assertEquals("wahaha", CacheUtil.localCache("test").get("nihao"));
    }
}