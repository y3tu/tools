package com.y3tu.tools.web.cache;

import com.y3tu.tools.kit.exception.ToolException;
import com.y3tu.tools.kit.lang.Assert;
import com.y3tu.tools.kit.lang.Singleton;
import com.y3tu.tools.web.cache.annotation.LocalCache;
import com.y3tu.tools.web.cache.annotation.RedisCache;
import com.y3tu.tools.web.cache.core.Cache;
import com.y3tu.tools.web.cache.manager.CacheManager;
import com.y3tu.tools.web.cache.manager.LayerCacheManager;
import com.y3tu.tools.web.cache.manager.LocalCacheManager;
import com.y3tu.tools.web.cache.manager.RedisCacheManager;
import com.y3tu.tools.web.cache.setting.CacheMode;
import com.y3tu.tools.web.cache.setting.LayerCacheSetting;
import com.y3tu.tools.web.cache.setting.LocalCacheSetting;
import com.y3tu.tools.web.cache.setting.RedisCacheSetting;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 缓存工具类
 *
 * @author y3tu
 */
public class CacheUtil {

    /**
     * 构建本地缓存对象
     *
     * @param name              缓存名称
     * @param stats             是否开启缓存统计
     * @param allowNullValue    是否允许缓存值为空
     * @param localCacheSetting 本地缓存配置
     * @return 本地Cache
     */
    public static Cache localCache(String name, boolean stats, boolean allowNullValue, LocalCacheSetting localCacheSetting) {
        CacheManager cacheManager = buildCacheManager(stats, allowNullValue, CacheMode.ONLY_LOCAL, null);
        Cache cache = cacheManager.getCache(name, buildCacheSetting(localCacheSetting, null, ""));
        return cache;
    }

    /**
     * 构建默认配置的本地缓存
     *
     * @param name 缓存名称
     * @return 本地Cache
     */
    public static Cache localCache(String name) {
        return localCache(name, true, true, new LocalCacheSetting());
    }

    /**
     * 构建Redis缓存对象
     *
     * @param name              缓存名
     * @param stats             是否开启缓存统计
     * @param allowNullValue    是否允许缓存值为空
     * @param redisTemplate     RedisTemplate
     * @param redisCacheSetting 缓存配置
     * @return Redis缓存对象
     */
    public static Cache redisCache(String name, boolean stats, boolean allowNullValue, RedisTemplate<String, Object> redisTemplate, RedisCacheSetting redisCacheSetting) {
        CacheManager cacheManager = buildCacheManager(stats, allowNullValue, CacheMode.ONLY_REDIS, redisTemplate);
        Cache cache = cacheManager.getCache(name, buildCacheSetting(null, redisCacheSetting, ""));
        return cache;
    }

    /**
     * 构建Redis缓存对象
     *
     * @param name          缓存名
     * @param redisTemplate RedisTemplate
     * @return Redis缓存对象
     */
    public static Cache redisCache(String name, RedisTemplate<String, Object> redisTemplate) {
        return redisCache(name, true, true, redisTemplate, new RedisCacheSetting());
    }

    /**
     * 构建多级缓存对象
     *
     * @param name              缓存名
     * @param stats             是否开启缓存统计
     * @param allowNullValue    是否允许缓存值为空
     * @param redisTemplate     RedisTemplate
     * @param layerCacheSetting 多级缓存配置
     * @return 多级缓存对象
     */
    public static Cache layerCache(String name, boolean stats, boolean allowNullValue, RedisTemplate<String, Object> redisTemplate, LayerCacheSetting layerCacheSetting) {
        CacheManager cacheManager = buildCacheManager(stats, allowNullValue, CacheMode.ALL, redisTemplate);
        Cache cache = cacheManager.getCache(name, layerCacheSetting);
        return cache;
    }

    /**
     * 构建多级缓存对象
     *
     * @param name          缓存名
     * @param redisTemplate RedisTemplate
     * @return 多级缓存对象
     */
    public static Cache layerCache(String name, RedisTemplate<String, Object> redisTemplate) {
        return layerCache(name, true, true, redisTemplate, new LayerCacheSetting());
    }

    /**
     * 构建CacheManager对象
     *
     * @param stats          是否开启缓存统计
     * @param allowNullValue 是否允许缓存空值
     * @param cacheMode      缓存模式
     * @param redisTemplate  RedisTemplate对象
     * @return CacheManager
     */
    public static CacheManager buildCacheManager(boolean stats, boolean allowNullValue, CacheMode cacheMode, RedisTemplate<String, Object> redisTemplate) {
        CacheManager cacheManager;
        try {
            if (cacheMode == CacheMode.ONLY_LOCAL) {
                //只开启本地缓存
                cacheManager = Singleton.get(LocalCacheManager.class, stats, allowNullValue);
            } else if (cacheMode == CacheMode.ONLY_REDIS) {
                Assert.notNull(redisTemplate, "redisTemplate不能为空");
                //只开启Redis缓存
                cacheManager = Singleton.get(RedisCacheManager.class, stats, allowNullValue, redisTemplate);
            } else {
                //开启多级缓存
                Assert.notNull(redisTemplate, "redisTemplate不能为空");
                cacheManager = Singleton.get(LayerCacheManager.class, stats, allowNullValue, redisTemplate);
            }
        } catch (NoClassDefFoundError e) {
            throw new ToolException("缓存需要添加Redis依赖！");
        }
        return cacheManager;
    }


    /**
     * 构建多级缓存配置项
     *
     * @param localCache 本地缓存注解
     * @param redisCache Redis缓存注解
     * @return {@link LayerCacheSetting}
     */
    public static LayerCacheSetting buildCacheSetting(LocalCache localCache, RedisCache redisCache, String description) {
        LocalCacheSetting localCacheSetting = new LocalCacheSetting(localCache.initialCapacity(), localCache.maximumSize(),
                localCache.expireTime(), localCache.dateUnit(), localCache.expireMode());

        RedisCacheSetting redisCacheSetting = new RedisCacheSetting(redisCache.expireTime(),
                redisCache.preloadTime(), redisCache.dateUnit(), redisCache.forceRefresh(), true,
                redisCache.isAllowNullValue(), redisCache.magnification());

        LayerCacheSetting layerCacheSetting = new LayerCacheSetting(true, localCacheSetting, redisCacheSetting, description);
        return layerCacheSetting;
    }

    /**
     * 构建多级缓存配置项
     *
     * @param localCacheSetting 本地缓存配置
     * @param redisCacheSetting Redis缓存配置
     * @return {@link LayerCacheSetting}
     */
    public static LayerCacheSetting buildCacheSetting(LocalCacheSetting localCacheSetting, RedisCacheSetting redisCacheSetting, String description) {
        LayerCacheSetting layerCacheSetting = new LayerCacheSetting(true, localCacheSetting, redisCacheSetting, description);
        return layerCacheSetting;
    }
}
