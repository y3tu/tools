package com.y3tu.tools.web.cache.staticdata.handler;


import com.y3tu.tools.web.cache.core.Cache;

/**
 * 对静态数据进行处理
 *
 * @author y3tu
 */
public interface StaticDataHandler {
    /**
     * 处理缓存
     *
     * @param key       缓存key
     * @param cache     缓存处理接口
     * @param cacheData 缓存数据
     * @return
     */
    void handler(String key, Cache cache, Object cacheData);
}
