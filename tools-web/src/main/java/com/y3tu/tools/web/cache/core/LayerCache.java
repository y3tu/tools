package com.y3tu.tools.web.cache.core;

import com.y3tu.tools.kit.base.JsonUtil;
import com.y3tu.tools.kit.lang.func.Func0;
import com.y3tu.tools.web.cache.core.caffeine.CaffeineCache;
import com.y3tu.tools.web.cache.core.redis.RedisCache;
import com.y3tu.tools.web.cache.listener.RedisPubSubMessage;
import com.y3tu.tools.web.cache.listener.RedisPubSubMessageType;
import com.y3tu.tools.web.cache.listener.RedisPublisher;
import com.y3tu.tools.web.cache.setting.LayerCacheSetting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

/**
 * 多级缓存实现
 *
 * @author y3tu
 */
@Slf4j
public class LayerCache extends BaseCache {

    /**
     * redis客户端
     */
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 本地缓存
     */
    private CaffeineCache localCache;

    /**
     * redis缓存
     */
    private RedisCache redisCache;
    /**
     * 是否使用一级缓存， 默认true
     */
    private boolean useLocalCache = true;
    /**
     * 多级缓存配置
     */
    private LayerCacheSetting layerCacheSetting;


    /**
     * 构建多级缓存
     *
     * @param redisTemplate     redis客户端
     * @param localCache        本地缓存
     * @param redisCache        redis缓存
     * @param useLocalCache     是否使用本地缓存
     * @param name              缓存名称
     * @param stats             是否开启统计
     * @param allowNullValue    是否允许缓存为空值
     * @param layerCacheSetting 多级缓存配置
     */
    public LayerCache(RedisTemplate<String, Object> redisTemplate, CaffeineCache localCache,
                      RedisCache redisCache, boolean useLocalCache, String name, boolean stats, boolean allowNullValue, LayerCacheSetting layerCacheSetting) {
        super(name, stats, allowNullValue);
        this.redisTemplate = redisTemplate;
        this.localCache = localCache;
        this.redisCache = redisCache;
        this.useLocalCache = useLocalCache;
        this.layerCacheSetting = layerCacheSetting;
    }

    @Override
    public Object getNativeCache() {
        return this;
    }

    @Override
    public Object get(Object key) {
        Object result = null;
        if (useLocalCache) {
            result = localCache.get(key);
            log.debug("查询本地缓存。 key={},返回值是:{}", key, JsonUtil.toJson(result));
        }
        if (result == null) {
            result = redisCache.get(key);
            localCache.putIfAbsent(key, result);
            log.debug("查询Redis缓存,并将数据放到本地缓存。 key={},返回值是:{}", key, JsonUtil.toJson(result));
        }
        return fromStoreValue(result);
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        if (useLocalCache) {
            Object result = localCache.get(key, type);
            log.debug("查询本地缓存。 key={},返回值是:{}", key, JsonUtil.toJson(result));
            if (result != null) {
                return (T) fromStoreValue(result);
            }
        }

        T result = redisCache.get(key, type);
        localCache.putIfAbsent(key, result);
        log.debug("查询Redis缓存,并将数据放到本地缓存。 key={},返回值是:{}", key, JsonUtil.toJson(result));
        return result;
    }

    @Override
    public <T> T get(Object key, Func0<T> valueLoader) {
        if (useLocalCache) {
            Object result = localCache.get(key);
            log.debug("查询本地缓存。 key={},返回值是:{}", key, JsonUtil.toJson(result));
            if (result != null) {
                return (T) fromStoreValue(result);
            }
        }
        T result = redisCache.get(key, valueLoader);
        localCache.putIfAbsent(key, result);
        log.debug("查询Redis缓存,并将数据放到本地缓存。 key={},返回值是:{}", key, JsonUtil.toJson(result));
        return result;
    }

    @Override
    public void put(Object key, Object value) {
        redisCache.put(key, value);
        // 删除一级缓存
        if (useLocalCache) {
            deleteLocalCache(key);
        }
    }

    @Override
    public Object putIfAbsent(Object key, Object value) {
        Object result = redisCache.putIfAbsent(key, value);
        // 删除本地缓存
        if (useLocalCache) {
            deleteLocalCache(key);
        }
        return result;
    }

    @Override
    public void evict(Object key) {
        // 删除的时候要先删除Redis缓存再删除本地缓存，否则有并发问题
        redisCache.evict(key);
        // 删除本地缓存
        if (useLocalCache) {
            deleteLocalCache(key);
        }
    }

    @Override
    public void clear() {
        // 删除的时候要先删除Redis缓存再删除本地缓存，否则有并发问题
        redisCache.clear();
        if (useLocalCache) {
            // 清除一级缓存需要用到redis的订阅/发布模式，否则集群中其他服服务器节点的一级缓存数据无法删除
            RedisPubSubMessage message = new RedisPubSubMessage();
            message.setCacheName(getName());
            message.setMessageType(RedisPubSubMessageType.CLEAR);
            // 发布消息
            RedisPublisher.publisher(redisTemplate, new ChannelTopic(getName()), message);
        }
    }

    /**
     * 删除本地缓存
     *
     * @param key 键
     */
    private void deleteLocalCache(Object key) {
        // 删除一级缓存需要用到redis的Pub/Sub（订阅/发布）模式，否则集群中其他服服务器节点的一级缓存数据无法删除
        RedisPubSubMessage message = new RedisPubSubMessage();
        message.setCacheName(getName());
        message.setKey(key);
        message.setMessageType(RedisPubSubMessageType.EVICT);
        // 发布消息
        RedisPublisher.publisher(redisTemplate, new ChannelTopic(getName()), message);
    }

    public CaffeineCache getLocalCache() {
        return localCache;
    }

    public RedisCache getRedisCache() {
        return redisCache;
    }

    public LayerCacheSetting getLayerCacheSetting() {
        return layerCacheSetting;
    }
}
