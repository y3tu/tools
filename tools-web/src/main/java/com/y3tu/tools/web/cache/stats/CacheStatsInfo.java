package com.y3tu.tools.web.cache.stats;

import com.y3tu.tools.web.cache.setting.LayerCacheSetting;
import com.y3tu.tools.web.cache.setting.LocalCacheSetting;
import com.y3tu.tools.web.cache.setting.RedisCacheSetting;
import lombok.Data;

import java.io.Serializable;

/**
 * 缓存命中率统计实体类
 *
 * @author y3tu
 */
@Data
public class CacheStatsInfo implements Serializable {

    /**
     * 缓存名称
     */
    private String cacheName;

    /**
     * 描述
     */
    private String description;

    /**
     * 总请求总数
     */
    private long requestCount;

    /**
     * 总未命中总数
     */
    private long missCount;

    /**
     * 命中率
     */
    private double hitRate;

    /**
     * 本地缓存命中总数
     */
    private long localCacheRequestCount;

    /**
     * 本地缓存未命中总数
     */
    private long localCacheMissCount;

    /**
     * Redis缓存命中总数
     */
    private long redisCacheRequestCount;

    /**
     * Redis缓存未命中总数
     */
    private long redisCacheMissCount;

    /**
     * 总的请求时间
     */
    private long totalLoadTime;

    /**
     * 本地缓存配置
     */
    private LocalCacheSetting localCacheSetting;
    /**
     * Redis缓存配置
     */
    private RedisCacheSetting redisCacheSetting;
    /**
     * 多级缓存配置
     */
    private LayerCacheSetting layerCacheSetting;


    /**
     * 清空统计信息
     */
    public void clearStatsInfo() {
        this.setRequestCount(0);
        this.setMissCount(0);
        this.setTotalLoadTime(0);
        this.setHitRate(0);

        this.setLocalCacheRequestCount(0);
        this.setLocalCacheMissCount(0);

        this.setRedisCacheRequestCount(0);
        this.setRedisCacheMissCount(0);
    }
}
