package com.y3tu.tools.web.cache.manager;

import com.y3tu.tools.kit.base.JsonUtil;
import com.y3tu.tools.kit.concurrent.thread.ThreadUtil;
import com.y3tu.tools.kit.text.StrUtil;
import com.y3tu.tools.web.cache.core.Cache;
import com.y3tu.tools.web.cache.core.redis.RedisCache;
import com.y3tu.tools.web.cache.setting.LayerCacheSetting;
import com.y3tu.tools.web.cache.setting.RedisCacheSetting;
import com.y3tu.tools.web.cache.stats.CacheStats;
import com.y3tu.tools.web.cache.stats.CacheStatsInfo;
import com.y3tu.tools.web.redis.service.RedisService;
import com.y3tu.tools.web.redis.support.Lock;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 二级缓存管理
 *
 * @author y3tu
 */
@Slf4j
@Data
public class RedisCacheManager extends BaseCacheManager implements DisposableBean {

    /**
     * 缓存统计数据前缀
     */
    public static final String CACHE_STATS_KEY_PREFIX = "cache:cache_stats_info:tools:";

    /**
     * redis操作
     */
    RedisTemplate<String, Object> redisTemplate;

    /**
     * 同步缓存统计线程池
     */
    ScheduledExecutorService executor;

    /**
     * 构建
     *
     * @param stats          是否开启统计
     * @param allowNullValue 是否允许值为空
     */
    public RedisCacheManager(boolean stats, boolean allowNullValue) {
        this.stats = stats;
        this.allowNullValue = allowNullValue;
        this.redisTemplate = redisTemplate;
        if (stats) {
            syncCacheStats();
        }
    }

    public RedisCacheManager() {
    }

    @Override
    protected Cache getMissingCache(String name, LayerCacheSetting layerCacheSetting) {
        return getMissingCache(name, layerCacheSetting.getRedisCacheSetting());
    }

    public Cache getMissingCache(String name, RedisCacheSetting redisCacheSetting) {
        // 创建Redis缓存
        RedisCache redisCache = new RedisCache(name, isStats(), isAllowNullValue(), redisTemplate, redisCacheSetting);
        return redisCache;
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
        log.debug("获取缓存统计数据");

        Set<String> layeringCacheKeys = new RedisService(this.getRedisTemplate()).scan(CACHE_STATS_KEY_PREFIX + "*");
        if (CollectionUtils.isEmpty(layeringCacheKeys)) {
            return Collections.emptyList();
        }
        // 遍历找出对应统计数据
        List<CacheStatsInfo> statsList = new ArrayList<>();
        for (String key : layeringCacheKeys) {
            if (StrUtil.isNotBlank(cacheName) && !key.startsWith(CACHE_STATS_KEY_PREFIX + cacheName)) {
                continue;
            }

            CacheStatsInfo cacheStats = (CacheStatsInfo) this.getRedisTemplate().opsForValue().get(key);
            if (!Objects.isNull(cacheStats)) {
                statsList.add(cacheStats);
            }
        }

        return statsList.stream().sorted(Comparator.comparing(CacheStatsInfo::getHitRate)).collect(Collectors.toList());
    }

    @Override
    public void resetCacheStat() {
        RedisTemplate<String, Object> redisTemplate = this.getRedisTemplate();
        Set<String> layeringCacheKeys = new RedisService(this.getRedisTemplate()).scan(CACHE_STATS_KEY_PREFIX + "*");
        for (String key : layeringCacheKeys) {
            resetCacheStatByKey(key);
        }
    }

    private void resetCacheStatByKey(String key) {
        RedisTemplate<String, Object> redisTemplate = this.getRedisTemplate();
        try {
            CacheStatsInfo cacheStats = (CacheStatsInfo) redisTemplate.opsForValue().get(key);
            if (Objects.nonNull(cacheStats)) {
                cacheStats.clearStatsInfo();
                // 将缓存统计数据写到redis
                redisTemplate.opsForValue().set(key, cacheStats, 24, TimeUnit.HOURS);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void resetCacheStat(String cacheName) {
        String key = CACHE_STATS_KEY_PREFIX + cacheName;
        resetCacheStatByKey(key);
    }

    @Override
    public void destroy() throws Exception {
        if (executor != null) {
            executor.shutdown();
        }
    }

    @Override
    public void clearCache(String cacheName) {
        Cache cache = getCache(cacheName);
        //删除缓存统计
        String redisKey = CACHE_STATS_KEY_PREFIX + cacheName;
        redisTemplate.delete(redisKey);
        //清空缓存
        cache.clear();
    }

    /**
     * 同步缓存统计list
     */
    public void syncCacheStats() {
        RedisTemplate<String, Object> redisTemplate = this.getRedisTemplate();
        // 清空统计数据
        resetCacheStat();
        //定时任务线程池
        executor = ThreadUtil.newScheduledExecutor(1, ThreadUtil.newNamedThreadFactory("缓存统计服务线程池", true));
        executor.scheduleAtFixedRate(() -> {
            log.debug("执行缓存统计数据采集定时任务");
            // 获取CacheManager
            Collection<String> cacheNames = this.getCacheNames();
            for (String cacheName : cacheNames) {
                // 获取Cache
                Cache cache = this.getCache(cacheName);
                RedisCache redisCache = (RedisCache) cache;
                RedisCacheSetting redisCacheSetting = redisCache.getRedisCacheSetting();
                // 加锁并增量缓存统计数据，缓存key=固定前缀 +缓存名称
                String redisKey = CACHE_STATS_KEY_PREFIX + cacheName;
                Lock lock = new Lock(redisTemplate, redisKey, 60, 5000);
                try {
                    if (lock.tryLock()) {
                        CacheStatsInfo cacheStatsInfo = (CacheStatsInfo) redisTemplate.opsForValue().get(redisKey);
                        if (Objects.isNull(cacheStatsInfo)) {
                            cacheStatsInfo = new CacheStatsInfo();
                        }

                        // 设置缓存唯一标示
                        cacheStatsInfo.setCacheName(cacheName);
                        // 设置缓存配置信息
                        cacheStatsInfo.setRedisCacheSetting(redisCacheSetting);
                        // 设置缓存统计数据
                        CacheStats cacheStats = redisCache.getCacheStats();


                        // 清空加载缓存时间
                        cacheStats.getAndResetCachedMethodRequestTime();

                        cacheStatsInfo.setRequestCount(cacheStatsInfo.getRequestCount() + cacheStats.getAndResetCacheRequestCount());
                        cacheStatsInfo.setMissCount(cacheStatsInfo.getMissCount() + cacheStats.getAndResetCachedMethodRequestCount());
                        cacheStatsInfo.setTotalLoadTime(cacheStatsInfo.getTotalLoadTime() + cacheStats.getAndResetCachedMethodRequestTime());
                        cacheStatsInfo.setHitRate((cacheStatsInfo.getRequestCount() - cacheStatsInfo.getMissCount()) / (double) cacheStatsInfo.getRequestCount() * 100);
                        cacheStatsInfo.setRedisCacheRequestCount(cacheStatsInfo.getRedisCacheRequestCount() + cacheStats.getAndResetCacheRequestCount());
                        cacheStatsInfo.setRedisCacheMissCount(cacheStatsInfo.getRedisCacheMissCount() + cacheStats.getAndResetCachedMethodRequestCount());

                        // 将缓存统计数据写到redis
                        redisTemplate.opsForValue().set(redisKey, cacheStatsInfo, 24, TimeUnit.HOURS);

                        log.info("Layering Cache 统计信息：{}", JsonUtil.toJson(cacheStatsInfo));
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                } finally {
                    lock.unlock();
                }
            }

            //  初始时间间隔是1分
        }, 1, 1, TimeUnit.MINUTES);
    }
}
