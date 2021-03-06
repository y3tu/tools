package com.y3tu.tools.web.cache.manager;

import com.y3tu.tools.kit.base.JsonUtil;
import com.y3tu.tools.kit.concurrent.thread.ThreadUtil;
import com.y3tu.tools.web.cache.core.Cache;
import com.y3tu.tools.web.cache.core.LayerCache;
import com.y3tu.tools.web.cache.core.caffeine.CaffeineCache;
import com.y3tu.tools.web.cache.core.redis.RedisCache;
import com.y3tu.tools.web.cache.listener.RedisMessageListener;
import com.y3tu.tools.web.cache.setting.LayerCacheSetting;
import com.y3tu.tools.web.cache.stats.CacheStats;
import com.y3tu.tools.web.cache.stats.CacheStatsInfo;
import com.y3tu.tools.web.redis.support.Lock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.SmartLifecycle;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 多级缓存管理
 *
 * @author y3tu
 */
@Slf4j
public class LayerCacheManager extends RedisCacheManager implements InitializingBean, DisposableBean, BeanNameAware, SmartLifecycle {

    /**
     * redis pub/sub 容器
     */
    private final RedisMessageListenerContainer container = new RedisMessageListenerContainer();

    /**
     * redis pub/sub 监听器
     */
    private final RedisMessageListener messageListener = new RedisMessageListener();

    public LayerCacheManager(boolean stats, boolean allowNullValue, RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.stats = stats;
        this.allowNullValue = allowNullValue;
        if (isStats()) {
            syncCacheStats();
        }
    }

    /**
     * 根据缓存名称在CacheManager中没有找到对应Cache时，通过该方法新建一个对应的Cache实例
     *
     * @param name              缓存名称
     * @param layerCacheSetting 缓存配置
     * @return {@link Cache}
     */
    @Override
    public Cache getMissingCache(String name, LayerCacheSetting layerCacheSetting) {
        // 创建本地缓存
        CaffeineCache caffeineCache = new CaffeineCache(name, isStats(), isAllowNullValue(), layerCacheSetting.getLocalCacheSetting());
        // 创建Redis缓存
        RedisCache redisCache = new RedisCache(name, isStats(), isAllowNullValue(), redisTemplate, layerCacheSetting.getRedisCacheSetting());
        return new LayerCache(redisTemplate, caffeineCache, redisCache, layerCacheSetting.isUseLocalCache(), name, isStats(), isAllowNullValue(), layerCacheSetting);
    }

    /**
     * 添加消息监听
     *
     * @param cacheName 缓存名称
     */
    protected void addMessageListener(String cacheName) {
        container.addMessageListener(messageListener, new ChannelTopic(cacheName));
    }

    @Override
    public Cache getCache(String cacheName, LayerCacheSetting layeringCacheSetting) {
        // 第一次获取缓存Cache，如果有直接返回,如果没有加锁往容器里里面放Cache
        Cache cache = this.cacheContainer.get(cacheName);
        if (cache != null) {
            return cache;
        }
        // 第二次获取缓存Cache，加锁往容器里里面放Cache
        synchronized (this.cacheContainer) {
            // 新建一个Cache对象
            cache = getMissingCache(cacheName, layeringCacheSetting);
            if (cache != null) {
                // 装饰Cache对象
                cache = decorateCache(cache);
                // 将新的Cache对象放到容器
                this.cacheContainer.put(cacheName, cache);
            }
            //更新缓存名称
            updateCacheName(cacheName);
            // 创建redis监听
            addMessageListener(cacheName);
            return cache;
        }
    }

    /**
     * 同步缓存统计list
     */
    @Override
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
                LayerCache layerCache = (LayerCache) cache;
                LayerCacheSetting layerCacheSetting = layerCache.getLayerCacheSetting();
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
                        cacheStatsInfo.setLayerCacheSetting(layerCacheSetting);

                        // 设置缓存统计数据
                        CacheStats layeringCacheStats = layerCache.getCacheStats();
                        CacheStats localCacheStats = layerCache.getLocalCache().getCacheStats();
                        CacheStats redisCacheStats = layerCache.getRedisCache().getCacheStats();

                        // 清空加载缓存时间
                        localCacheStats.getAndResetCachedMethodRequestTime();
                        redisCacheStats.getAndResetCachedMethodRequestTime();

                        cacheStatsInfo.setRequestCount(cacheStatsInfo.getRequestCount() + layeringCacheStats.getAndResetCacheRequestCount());
                        cacheStatsInfo.setMissCount(cacheStatsInfo.getMissCount() + layeringCacheStats.getAndResetCachedMethodRequestCount());
                        cacheStatsInfo.setTotalLoadTime(cacheStatsInfo.getTotalLoadTime() + layeringCacheStats.getAndResetCachedMethodRequestTime());
                        cacheStatsInfo.setHitRate((cacheStatsInfo.getRequestCount() - cacheStatsInfo.getMissCount()) / (double) cacheStatsInfo.getRequestCount() * 100);

                        cacheStatsInfo.setLocalCacheRequestCount(cacheStatsInfo.getLocalCacheRequestCount() + localCacheStats.getAndResetCacheRequestCount());
                        cacheStatsInfo.setLocalCacheMissCount(cacheStatsInfo.getLocalCacheMissCount() + localCacheStats.getAndResetCachedMethodRequestCount());

                        cacheStatsInfo.setRedisCacheRequestCount(cacheStatsInfo.getRedisCacheRequestCount() + redisCacheStats.getAndResetCacheRequestCount());
                        cacheStatsInfo.setRedisCacheMissCount(cacheStatsInfo.getRedisCacheMissCount() + redisCacheStats.getAndResetCachedMethodRequestCount());

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

    @Override
    public void afterPropertiesSet() throws Exception {
        messageListener.setCacheManager(this);
        container.setConnectionFactory(getRedisTemplate().getConnectionFactory());
        container.afterPropertiesSet();
        messageListener.afterPropertiesSet();
    }

    @Override
    public void setBeanName(String name) {
        container.setBeanName("redisMessageListenerContainer");
    }

    @Override
    public boolean isAutoStartup() {
        return container.isAutoStartup();
    }

    @Override
    public void stop(Runnable callback) {
        container.stop(callback);
    }

    @Override
    public void start() {
        container.start();
    }

    @Override
    public void stop() {
        container.stop();
    }

    @Override
    public boolean isRunning() {
        return container.isRunning();
    }

    @Override
    public int getPhase() {
        return container.getPhase();
    }

    @Override
    public void destroy() throws Exception {
        container.destroy();
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

}
