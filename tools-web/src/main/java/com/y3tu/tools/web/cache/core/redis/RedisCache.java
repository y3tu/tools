package com.y3tu.tools.web.cache.core.redis;

import com.y3tu.tools.kit.base.JsonUtil;
import com.y3tu.tools.kit.concurrent.thread.AwaitThreadContainer;
import com.y3tu.tools.kit.exception.ToolException;
import com.y3tu.tools.kit.lang.func.Func0;
import com.y3tu.tools.kit.time.DateUnit;
import com.y3tu.tools.web.cache.core.BaseCache;
import com.y3tu.tools.web.cache.core.CacheThreadTask;
import com.y3tu.tools.web.cache.core.NullValue;
import com.y3tu.tools.web.cache.setting.RedisCacheSetting;
import com.y3tu.tools.web.redis.service.RedisService;
import com.y3tu.tools.web.redis.support.Lock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * 基于redis的缓存实现
 *
 * @author y3tu
 */
@Slf4j
public class RedisCache extends BaseCache {

    /**
     * 刷新缓存重试次数
     */
    private static final int RETRY_COUNT = 20;

    /**
     * 刷新缓存等待时间
     */
    private static final long WAIT_TIME = 10;
    /**
     * 等待线程容器
     */
    private AwaitThreadContainer container = new AwaitThreadContainer();
    /**
     * redis 客户端
     */
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 缓存配置
     */
    private RedisCacheSetting redisCacheSetting;

    /**
     * 构建方法
     *
     * @param name              缓存名
     * @param stats             是否开启统计
     * @param allowNullValue    是否允许空值
     * @param redisTemplate     {@link RedisTemplate}
     * @param redisCacheSetting 缓存配置
     */
    public RedisCache(String name, boolean stats, boolean allowNullValue, RedisTemplate<String, Object> redisTemplate, RedisCacheSetting redisCacheSetting) {
        super(name, stats, allowNullValue);
        Assert.notNull(redisTemplate, "RedisTemplate 不能为NULL");
        this.redisTemplate = redisTemplate;
        this.allowNullValue = redisCacheSetting.isAllowNullValue();
        this.redisCacheSetting = redisCacheSetting;
    }

    @Override
    public Object getNativeCache() {
        return this.redisTemplate;
    }

    @Override
    public Object get(Object key) {
        if (isStats()) {
            getCacheStats().addCacheRequestCount(1);
        }

        RedisCacheKey redisCacheKey = getRedisCacheKey(key);
        log.debug("redis缓存 key= {} 查询redis缓存", redisCacheKey.getKey());
        return redisTemplate.opsForValue().get(redisCacheKey.getKey());
    }

    @Override
    public <T> T get(Object key, Func0<T> valueLoader) {
        if (isStats()) {
            getCacheStats().addCacheRequestCount(1);
        }

        RedisCacheKey redisCacheKey = getRedisCacheKey(key);
        log.debug("redis缓存 key= {} 查询redis缓存如果没有命中，从数据库获取数据", redisCacheKey.getKey());
        // 先获取缓存，如果有直接返回
        Object result = redisTemplate.opsForValue().get(redisCacheKey.getKey());
        if (result != null || redisTemplate.hasKey(redisCacheKey.getKey())) {
            // 刷新缓存
            refreshCache(redisCacheKey, valueLoader, result);
            return (T) fromStoreValue(result);
        }
        // 执行缓存方法
        return executeCacheMethod(redisCacheKey, valueLoader);
    }

    @Override
    public void put(Object key, Object value) {
        RedisCacheKey redisCacheKey = getRedisCacheKey(key);
        log.debug("redis缓存 key= {} put缓存，缓存值：{}", redisCacheKey.getKey(), JsonUtil.toJson(value));
        putValue(redisCacheKey, value);
    }

    @Override
    public Object putIfAbsent(Object key, Object value) {
        log.debug("redis缓存 key= {} putIfAbsent缓存，缓存值：{}", getRedisCacheKey(key).getKey(), JsonUtil.toJson(value));
        Object result = get(key);
        if (result != null) {
            return result;
        }
        put(key, value);
        return null;
    }

    @Override
    public void evict(Object key) {
        RedisCacheKey redisCacheKey = getRedisCacheKey(key);
        log.info("清除redis缓存 key= {} ", redisCacheKey.getKey());
        redisTemplate.delete(redisCacheKey.getKey());
    }

    @Override
    public void clear() {
        // 必须开启了使用缓存名称作为前缀，clear才有效
        if (redisCacheSetting.isUsePrefix()) {
            log.info("清空redis缓存 ，缓存前缀为{}", getName());

            Set<String> keys = new RedisService(redisTemplate).scan(getName() + "*");
            if (!CollectionUtils.isEmpty(keys)) {
                redisTemplate.delete(keys);
            }
        }
    }

    /**
     * 获取 RedisCacheKey
     *
     * @param key 缓存key
     * @return RedisCacheKey
     */
    private RedisCacheKey getRedisCacheKey(Object key) {
        return new RedisCacheKey(key, redisTemplate.getKeySerializer()).cacheName(getName()).usePrefix(redisCacheSetting.isUsePrefix());
    }

    /**
     * 刷新缓存数据
     *
     * @param redisCacheKey key
     * @param valueLoader   值执行的方法
     * @param result        缓存值
     */
    private <T> void refreshCache(RedisCacheKey redisCacheKey, Func0<T> valueLoader, Object result) {
        Long ttl = redisTemplate.getExpire(redisCacheKey.getKey());
        Long preload = redisCacheSetting.getPreloadTime();
        // 允许缓存NULL值，则自动刷新时间也要除以倍数
        boolean flag = isAllowNullValue() && (result instanceof NullValue || result == null);
        if (flag) {
            preload = preload / redisCacheSetting.getMagnification();
        }
        if (null != ttl && ttl > 0 && ttl <= preload) {
            // 判断是否需要强制刷新在开启刷新线程
            if (!this.redisCacheSetting.isForceRefresh()) {
                log.debug("redis缓存 key={} 软刷新缓存模式", redisCacheKey.getKey());
                softRefresh(redisCacheKey);
            } else {
                log.debug("redis缓存 key={} 强刷新缓存模式", redisCacheKey.getKey());
                forceRefresh(redisCacheKey, valueLoader);
            }
        }
    }

    /**
     * 软刷新，直接修改缓存时间
     *
     * @param redisCacheKey {@link RedisCacheKey}
     */
    private void softRefresh(RedisCacheKey redisCacheKey) {
        // 加一个分布式锁，只放一个请求去刷新缓存
        Lock redisLock = new Lock(redisTemplate, redisCacheKey.getKey() + "_lock");
        try {
            if (redisLock.tryLock()) {
                redisTemplate.expire(redisCacheKey.getKey(), redisCacheSetting.getExpiration(), DateUnit.toTimeUnit(redisCacheSetting.getDateUnit()));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            redisLock.unlock();
        }
    }

    /**
     * 硬刷新（执行被缓存的方法）
     *
     * @param redisCacheKey {@link RedisCacheKey}
     * @param valueLoader   数据加载器
     */
    private <T> void forceRefresh(RedisCacheKey redisCacheKey, Func0<T> valueLoader) {
        // 尽量少的去开启线程，因为线程池是有限的
        CacheThreadTask.run(() -> {
            // 加一个分布式锁，只放一个请求去刷新缓存
            Lock redisLock = new Lock(redisTemplate, redisCacheKey.getKey() + "_lock");
            try {
                if (redisLock.lock()) {
                    // 获取锁之后再判断一下过期时间，看是否需要加载数据
                    Long ttl = redisTemplate.getExpire(redisCacheKey.getKey());
                    if (null != ttl && ttl > 0 && ttl <= redisCacheSetting.getPreloadTime()) {
                        // 加载数据并放到缓存
                        loaderAndPutValue(redisCacheKey, valueLoader, false);
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                redisLock.unlock();
            }
        });
    }

    /**
     * 同一个线程循环5次查询缓存，每次等待20毫秒，如果还是没有数据直接去执行被缓存的方法
     *
     * @param redisCacheKey key
     * @param valueLoader   获取值的方法
     */
    private <T> T executeCacheMethod(RedisCacheKey redisCacheKey, Func0<T> valueLoader) {
        Lock redisLock = new Lock(redisTemplate, redisCacheKey.getKey() + "_sync_lock");
        // 同一个线程循环20次查询缓存，每次等待20毫秒，如果还是没有数据直接去执行被缓存的方法
        for (int i = 0; i < RETRY_COUNT; i++) {
            try {
                // 先取缓存，如果有直接返回，没有再去做拿锁操作
                Object result = redisTemplate.opsForValue().get(redisCacheKey.getKey());
                if (result != null) {
                    log.debug("redis缓存 key= {} 获取到锁后查询查询缓存命中，不需要执行被缓存的方法", redisCacheKey.getKey());
                    return (T) fromStoreValue(result);
                }

                // 获取分布式锁去后台查询数据
                if (redisLock.lock()) {
                    T t = loaderAndPutValue(redisCacheKey, valueLoader, true);
                    log.debug("redis缓存 key= {} 从数据库获取数据完毕，唤醒所有等待线程", redisCacheKey.getKey());
                    // 唤醒线程
                    container.signalAll(redisCacheKey.getKey());
                    return t;
                }
                // 线程等待
                log.debug("redis缓存 key= {} 从数据库获取数据未获取到锁，进入等待状态，等待{}毫秒", redisCacheKey.getKey(), WAIT_TIME);
                container.await(redisCacheKey.getKey(), WAIT_TIME);
            } catch (Exception e) {
                container.signalAll(redisCacheKey.getKey());
                throw new ToolException(e, "key[%s]缓存加载异常！", redisCacheKey.getKey());
            } finally {
                redisLock.unlock();
            }
        }
        log.debug("redis缓存 key={} 等待{}次，共{}毫秒，任未获取到缓存，直接去执行被缓存的方法", redisCacheKey.getKey(), RETRY_COUNT, RETRY_COUNT * WAIT_TIME);
        return loaderAndPutValue(redisCacheKey, valueLoader, true);
    }


    /**
     * 加载并将数据放到redis缓存
     */
    private <T> T loaderAndPutValue(RedisCacheKey key, Func0<T> valueLoader, boolean isLoad) {
        long start = System.currentTimeMillis();
        if (isLoad && isStats()) {
            getCacheStats().addCachedMethodRequestCount(1);
        }

        try {
            // 加载数据
            Object result = putValue(key, valueLoader.call());
            log.debug("redis缓存 key={} 执行被缓存的方法，并将其放入缓存, 耗时：{}。数据:{}", key.getKey(), System.currentTimeMillis() - start, JsonUtil.toJson(result));

            if (isLoad && isStats()) {
                getCacheStats().addCachedMethodRequestTime(System.currentTimeMillis() - start);
            }
            return (T) fromStoreValue(result);
        } catch (Exception e) {
            throw new ToolException(e, "key[%s]缓存加载异常！", JsonUtil.toJson(key));
        }
    }

    /**
     * 放入数据到缓存
     *
     * @param key   键
     * @param value 值
     * @return 缓存数据
     */
    private Object putValue(RedisCacheKey key, Object value) {
        Object result = toStoreValue(value);
        // redis 缓存不允许直接存NULL，如果结果返回NULL需要删除缓存
        if (result == null) {
            redisTemplate.delete(key.getKey());
            return result;
        }
        // 不允许缓存NULL值，删除缓存
        if (!isAllowNullValue() && result instanceof NullValue) {
            redisTemplate.delete(key.getKey());
            return result;
        }

        // 允许缓存NULL值
        long expirationTime = redisCacheSetting.getExpiration();
        // 允许缓存NULL值且缓存为值为null时需要重新计算缓存时间
        if (isAllowNullValue() && result instanceof NullValue) {
            expirationTime = expirationTime / redisCacheSetting.getMagnification();
        }
        // 将数据放到缓存
        redisTemplate.opsForValue().set(key.getKey(), result, expirationTime, DateUnit.toTimeUnit(redisCacheSetting.getDateUnit()));
        return result;
    }

    public RedisCacheSetting getRedisCacheSetting() {
        return redisCacheSetting;
    }
}
