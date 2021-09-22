package com.y3tu.tools.web.cache.core;

import com.y3tu.tools.web.cache.stats.CacheStats;
import lombok.Data;
import org.springframework.util.Assert;

/**
 * Cache接口的抽象实现类
 *
 * @author y3tu
 */
@Data
public abstract class BaseCache implements Cache {
    /**
     * 缓存名称
     */
    protected String name;

    /**
     * 是否开启统计功能
     */
    protected boolean stats;
    /**
     * 是否允许空值
     */
    protected boolean allowNullValue;

    /**
     * 缓存统计类
     */
    protected CacheStats cacheStats = new CacheStats();

    /**
     * 通过构造方法设置缓存配置
     *
     * @param name  缓存名称
     * @param stats 是否开启监控统计
     * @param allowNullValue 是否允许空值
     */
    public BaseCache(String name, boolean stats, boolean allowNullValue) {
        Assert.notNull(name, "缓存名称不能为NULL");
        this.name = name;
        this.stats = stats;
        this.allowNullValue = allowNullValue;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Object key, Class<T> type) {
        return (T) fromStoreValue(get(key));
    }

    protected Object fromStoreValue(Object storeValue) {
        if (isAllowNullValue() && storeValue instanceof NullValue) {
            return null;
        }
        return storeValue;
    }

    protected Object toStoreValue(Object userValue) {
        if (isAllowNullValue() && userValue == null) {
            return NullValue.INSTANCE;
        }
        return userValue;
    }
}
