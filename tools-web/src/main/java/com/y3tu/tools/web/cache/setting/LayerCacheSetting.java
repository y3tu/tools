package com.y3tu.tools.web.cache.setting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 多级缓存配置
 *
 * @author y3tu
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LayerCacheSetting implements Serializable {
    /**
     * 是否使用本地缓存
     */
    boolean useLocalCache = true;
    /**
     * 本地缓存配置
     */
    private LocalCacheSetting localCacheSetting;

    /**
     * redis缓存配置
     */
    private RedisCacheSetting redisCacheSetting;

}
