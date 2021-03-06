package com.y3tu.tools.web.cache.manager;

import com.y3tu.tools.web.cache.core.Cache;
import com.y3tu.tools.web.cache.setting.LayerCacheSetting;
import com.y3tu.tools.web.cache.stats.CacheStatsInfo;

import java.util.Collection;
import java.util.List;

/**
 * 缓存管理器接口
 * 允许通过缓存名称来获的对应的 {@link Cache}.
 *
 * @author y3tu
 */
public interface CacheManager {

    /**
     * 根据缓存名称返回对应的缓存
     *
     * @param cacheName 缓存的名称 (不能为 {@code null})
     * @return 返回对应名称的Cache, 如果没找到返回 {@code null}
     */
    Cache getCache(String cacheName);

    /**
     * 根据缓存名称返回对应的{@link Cache}，如果没有找到就新建一个并放到容器
     *
     * @param cacheName            缓存名称
     * @param layerCacheSetting 多级缓存配置
     * @return {@link Cache}
     */
    Cache getCache(String cacheName, LayerCacheSetting layerCacheSetting);

    /**
     * 获取所有缓存名称的集合
     *
     * @return 所有缓存名称的集合
     */
    Collection<String> getCacheNames();

    /**
     * 获取所有缓存的统计信息
     *
     * @return
     */
    List<CacheStatsInfo> listCacheStats();

    /**
     * 获取缓存命中率统计信息
     *
     * @param cacheName 缓存名称，为Blank则查询全部
     * @return
     */
    List<CacheStatsInfo> listCacheStats(String cacheName);

    /**
     * 重置缓存统计数据
     */
    void resetCacheStat();

    /**
     * 根据缓存名称重置缓存统计数据
     *
     * @param cacheName 缓存名称
     */
    void resetCacheStat(String cacheName);

    /**
     * 根据缓存名称清空缓存
     *
     * @param cacheName 缓存名称
     */
    void clearCache(String cacheName);

    /**
     * 清空所有缓存
     */
    void clearCache();
}
