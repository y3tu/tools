package com.y3tu.tools.web.cache.setting;

import com.y3tu.tools.kit.time.DateUnit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * redis缓存配置
 *
 * @author y3tu
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RedisCacheSetting implements Serializable {
    /**
     * 缓存有效时间
     */
    private long expiration = 0;

    /**
     * 缓存主动在失效前强制刷新缓存的时间
     */
    private long preloadTime = 0;

    /**
     * 时间单位 {@link DateUnit}
     */
    private DateUnit dateUnit = DateUnit.MILLISECOND;

    /**
     * 是否强制刷新（走数据库），默认是false
     */
    private boolean forceRefresh = false;

    /**
     * 是否使用缓存名称作为 redis key 前缀
     */
    private boolean usePrefix = true;

    /**
     * 是否允许存NULL值
     */
    boolean allowNullValue = false;

    /**
     * 非空值和null值之间的时间倍率，默认是1。allowNullValue=true才有效
     * <p>
     * 如配置缓存的有效时间是200秒，倍率这设置成10，
     * 那么当缓存value为null时，缓存的有效时间将是20秒，非空时为200秒
     * </p>
     */
    int magnification = 1;
}
