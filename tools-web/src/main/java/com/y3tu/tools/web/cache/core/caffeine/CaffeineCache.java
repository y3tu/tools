package com.y3tu.tools.web.cache.core.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.y3tu.tools.kit.base.JsonUtil;
import com.y3tu.tools.kit.exception.ToolException;
import com.y3tu.tools.kit.lang.func.Func0;
import com.y3tu.tools.kit.time.DateUnit;
import com.y3tu.tools.web.cache.core.BaseCache;
import com.y3tu.tools.web.cache.core.NullValue;
import com.y3tu.tools.web.cache.setting.ExpireMode;
import com.y3tu.tools.web.cache.setting.LocalCacheSetting;
import com.y3tu.tools.web.cache.stats.CacheStats;
import lombok.extern.slf4j.Slf4j;

/**
 * 基于Caffeine的本地缓存
 *
 * @author y3tu
 */
@Slf4j
public class CaffeineCache extends BaseCache {
    /**
     * 缓存对象
     */
    private final Cache cache;
    /**
     * 缓存配置
     */
    private LocalCacheSetting localCacheSetting;

    /**
     * 构建CaffeineCache
     *
     * @param name              缓存名称
     * @param stats             是否开启统计
     * @param allowNullValue    是否允许空值
     * @param localCacheSetting 本地缓存配置
     */
    public CaffeineCache(String name, boolean stats, boolean allowNullValue, LocalCacheSetting localCacheSetting) {
        super(name, stats, allowNullValue);
        this.localCacheSetting = localCacheSetting;
        this.cache = getCache(localCacheSetting);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return this.cache;
    }

    @Override
    public Object get(Object key) {
        log.debug("caffeine缓存 key={} 获取缓存", JsonUtil.toJson(key));
        if (isStats()) {
            getCacheStats().addCacheRequestCount(1);
        }
        if (this.cache instanceof LoadingCache) {
            return ((LoadingCache<Object, Object>) this.cache).get(key);
        }
        return cache.getIfPresent(key);
    }

    @Override
    public <T> T get(Object key, Func0<T> valueLoader) {
        log.debug("caffeine缓存 key={} 获取缓存， 如果没有命中就走库加载缓存", JsonUtil.toJson(key));
        if (isStats()) {
            getCacheStats().addCacheRequestCount(1);
        }
        Object result = this.cache.get(key, k -> loaderValue(key, valueLoader));
        // 如果不允许存NULL值 直接删除NULL值缓存
        boolean isEvict = !isAllowNullValue() && (result == null || result instanceof NullValue);
        if (isEvict) {
            evict(key);
        }
        return (T) fromStoreValue(result);
    }

    @Override
    public void put(Object key, Object value) {
        // 允许存NULL值
        if (isAllowNullValue()) {
            log.debug("caffeine缓存 key={} put缓存，缓存值：{}", JsonUtil.toJson(key), JsonUtil.toJson(value));
            this.cache.put(key, toStoreValue(value));
            return;
        }

        // 不允许存NULL值
        if (value != null && value instanceof NullValue) {
            log.debug("caffeine缓存 key={} put缓存，缓存值：{}", JsonUtil.toJson(key), JsonUtil.toJson(value));
            this.cache.put(key, toStoreValue(value));
        }
        log.debug("缓存值为NULL并且不允许存NULL值，不缓存数据");
    }

    @Override
    public Object putIfAbsent(Object key, Object value) {
        log.debug("caffeine缓存 key={} putIfAbsent 缓存，缓存值：{}", JsonUtil.toJson(key), JsonUtil.toJson(value));
        boolean flag = !isAllowNullValue() && (value == null || value instanceof NullValue);
        if (flag) {
            return null;
        }
        Object result = this.cache.get(key, k -> toStoreValue(value));
        return fromStoreValue(result);
    }

    @Override
    public void evict(Object key) {
        log.debug("caffeine缓存 key={} 清除缓存", JsonUtil.toJson(key));
        this.cache.invalidate(key);
    }

    @Override
    public void clear() {
        log.debug("caffeine缓存清空");
        this.cache.invalidateAll();
        //重新赋值CacheStats对象
        this.setCacheStats(new CacheStats());
    }

    /**
     * 加载数据
     *
     * @param key         键
     * @param valueLoader 值加载方法
     * @return 加载后的值
     */
    private <T> Object loaderValue(Object key, Func0<T> valueLoader) {
        long start = System.currentTimeMillis();
        if (isStats()) {
            getCacheStats().addCachedMethodRequestCount(1);
        }

        try {
            T t = valueLoader.call();
            log.debug("caffeine缓存 key={} 从库加载缓存", JsonUtil.toJson(key));

            if (isStats()) {
                getCacheStats().addCachedMethodRequestTime(System.currentTimeMillis() - start);
            }
            return toStoreValue(t);
        } catch (Exception e) {
            throw new ToolException(e, "key[%s]缓存加载异常！", key);
        }

    }

    /**
     * 创建Caffeine缓存对象
     *
     * @param cacheSetting 缓存配置化
     * @return 缓存对象
     */
    private static Cache getCache(LocalCacheSetting cacheSetting) {
        // 根据配置创建Caffeine builder
        Caffeine<Object, Object> builder = Caffeine.newBuilder();
        builder.initialCapacity(cacheSetting.getInitSize());
        builder.maximumSize(cacheSetting.getMaxSize());
        if (ExpireMode.WRITE.equals(cacheSetting.getExpireMode())) {
            builder.expireAfterWrite(cacheSetting.getExpireTime(), DateUnit.toTimeUnit(cacheSetting.getDateUnit()));
        } else if (ExpireMode.ACCESS.equals(cacheSetting.getExpireMode())) {
            builder.expireAfterAccess(cacheSetting.getExpireTime(), DateUnit.toTimeUnit(cacheSetting.getDateUnit()));
        }
        // 根据Caffeine builder创建 Cache 对象
        return builder.build();
    }

    public LocalCacheSetting getLocalCacheSetting() {
        return this.localCacheSetting;
    }
}
