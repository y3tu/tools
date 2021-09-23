package com.y3tu.tools.web.cache.setting;

/**
 * 缓存模式
 *
 * @author y3tu
 */
public enum CacheMode {
    /**
     * 只开启本地缓存
     */
    ONLY_LOCAL("只用本地缓存"),

    /**
     * 只开启Redis缓存
     */
    ONLY_REDIS("只用Redis缓存"),

    /**
     * 开启多级缓存
     */
    ALL("开启多级缓存");

    private String label;

    CacheMode(String label) {
        this.label = label;
    }
}
