package com.y3tu.tools.web.cache.listener;

import lombok.Data;

import java.io.Serializable;

/**
 * redis pub/sub 消息
 *
 * @author y3tu
 */
@Data
public class RedisPubSubMessage implements Serializable {
    /**
     * 缓存名称
     */
    private String cacheName;

    /**
     * 缓存key
     */
    private Object key;

    /**
     * 消息类型
     */
    private RedisPubSubMessageType messageType;

}