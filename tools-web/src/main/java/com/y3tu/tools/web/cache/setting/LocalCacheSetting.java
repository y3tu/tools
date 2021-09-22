package com.y3tu.tools.web.cache.setting;

import com.y3tu.tools.kit.time.DateUnit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 本地缓存配置
 *
 * @author y3tu
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LocalCacheSetting implements Serializable {
    /**
     * 缓存初始Size
     */
    private int initSize = 10;
    /**
     * 缓存最大Size
     */
    private int maxSize = 500;
    /**
     * 缓存有效时间
     */
    private int expireTime = 0;
    /**
     * 缓存时间单位
     */
    private DateUnit dateUnit = DateUnit.MILLISECOND;
    /**
     * 缓存失效模式{@link ExpireMode}
     */
    private ExpireMode expireMode = ExpireMode.WRITE;

}
