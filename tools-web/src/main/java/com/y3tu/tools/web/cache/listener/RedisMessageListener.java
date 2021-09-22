package com.y3tu.tools.web.cache.listener;

import com.y3tu.tools.kit.base.JsonUtil;
import com.y3tu.tools.web.cache.core.Cache;
import com.y3tu.tools.web.cache.core.LayerCache;
import com.y3tu.tools.web.cache.manager.LayerCacheManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * redis消息的订阅者
 *
 * @author y3tu
 */
@Slf4j
public class RedisMessageListener extends MessageListenerAdapter {

    /**
     * 缓存管理器
     */
    private LayerCacheManager cacheManager;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        super.onMessage(message, pattern);
        // 解析订阅发布的信息，获取缓存的名称和缓存的key
        RedisPubSubMessage redisPubSubMessage = (RedisPubSubMessage) cacheManager.getRedisTemplate()
                .getValueSerializer().deserialize(message.getBody());
        log.debug("redis消息订阅者接收到频道【{}】发布的消息。消息内容：{}", new String(message.getChannel()), JsonUtil.toJson(redisPubSubMessage));

        // 根据缓存名称获取多级缓存
        Cache cache = cacheManager.getCache(redisPubSubMessage.getCacheName());
        // 判断缓存是否是多级缓存
        if (cache != null && cache instanceof LayerCache) {
            switch (redisPubSubMessage.getMessageType()) {
                case EVICT:
                    // 获取一级缓存，并删除一级缓存数据
                    ((LayerCache) cache).getLocalCache().evict(redisPubSubMessage.getKey());
                    log.info("删除一级缓存{}数据,key={}", redisPubSubMessage.getCacheName(), redisPubSubMessage.getKey());
                    break;

                case CLEAR:
                    // 获取一级缓存，并删除一级缓存数据
                    ((LayerCache) cache).getLocalCache().clear();
                    log.info("清除一级缓存{}数据", redisPubSubMessage.getCacheName());
                    break;

                default:
                    log.error("接收到没有定义的订阅消息频道数据");
                    break;
            }

        }
    }

    public void setCacheManager(LayerCacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
}
