package com.y3tu.tools.web.cache.manager;

import com.y3tu.tools.web.cache.core.Cache;
import com.y3tu.tools.web.cache.core.caffeine.CaffeineCache;
import com.y3tu.tools.web.cache.setting.LayerCacheSetting;
import com.y3tu.tools.web.cache.setting.LocalCacheSetting;
import com.y3tu.tools.web.cache.stats.CacheStats;
import com.y3tu.tools.web.cache.stats.CacheStatsInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 本地缓存管理
 *
 * @author y3tu
 */
@Slf4j
public class LocalCacheManager extends BaseCacheManager {

    /**
     * 构建
     *
     * @param stats          是否开启统计
     * @param allowNullValue 是否允许值为空
     */
    public LocalCacheManager(boolean stats, boolean allowNullValue) {
        this.stats = stats;
        this.allowNullValue = allowNullValue;
    }


    @Override
    public Cache getMissingCache(String name, LayerCacheSetting layerCacheSetting) {
        return getMissingCache(name, layerCacheSetting.getLocalCacheSetting());
    }

    public Cache getMissingCache(String name, LocalCacheSetting localCacheSetting) {
        // 创建本地缓存
        CaffeineCache caffeineCache = new CaffeineCache(name, stats, allowNullValue, localCacheSetting);
        return caffeineCache;
    }

    @Override
    public List<CacheStatsInfo> listCacheStats() {
        List<CacheStatsInfo> cacheStatsInfoList = new ArrayList<>();
        Collection<String> cacheNames = this.getCacheNames();
        for (String cacheName : cacheNames) {
            List<CacheStatsInfo> cacheStatsInfos = listCacheStats(cacheName);
            if (!cacheStatsInfos.isEmpty()) {
                cacheStatsInfoList.addAll(cacheStatsInfos);
            }
        }
        return cacheStatsInfoList;
    }

    @Override
    public List<CacheStatsInfo> listCacheStats(String cacheName) {
        List<CacheStatsInfo> cacheStatsInfoList = new ArrayList<>();

        Cache cache = this.getCache(cacheName);
        CaffeineCache caffeineCache = (CaffeineCache) cache;
        CacheStatsInfo cacheStatsInfo = new CacheStatsInfo();
        cacheStatsInfo.setCacheName(cacheName);
        cacheStatsInfo.setLocalCacheSetting(caffeineCache.getLocalCacheSetting());
        CacheStats cacheStats = caffeineCache.getCacheStats();
        cacheStats.getAndResetCachedMethodRequestTime();
        cacheStatsInfo.setRequestCount(cacheStatsInfo.getRequestCount() + cacheStats.getAndResetCacheRequestCount());
        cacheStatsInfo.setMissCount(cacheStatsInfo.getMissCount() + cacheStats.getAndResetCachedMethodRequestCount());
        cacheStatsInfo.setTotalLoadTime(cacheStatsInfo.getTotalLoadTime() + cacheStats.getAndResetCachedMethodRequestTime());
        cacheStatsInfo.setHitRate((cacheStatsInfo.getRequestCount() - cacheStatsInfo.getMissCount()) / (double) cacheStatsInfo.getRequestCount() * 100);
        cacheStatsInfo.setLocalCacheRequestCount(cacheStatsInfo.getLocalCacheRequestCount() + cacheStats.getAndResetCacheRequestCount());
        cacheStatsInfo.setLocalCacheMissCount(cacheStatsInfo.getLocalCacheMissCount() + cacheStats.getAndResetCachedMethodRequestCount());

        cacheStatsInfoList.add(cacheStatsInfo);

        return cacheStatsInfoList;
    }

    @Override
    public void resetCacheStat() {
        Collection<String> cacheNames = this.getCacheNames();
        cacheNames.stream().forEach(cacheName -> {
            resetCacheStat(cacheName);
        });
    }

    @Override
    public void resetCacheStat(String cacheName) {
        Cache cache = this.getCache(cacheName);
        CaffeineCache caffeineCache = (CaffeineCache) cache;
        caffeineCache.setCacheStats(new CacheStats());
    }

}
