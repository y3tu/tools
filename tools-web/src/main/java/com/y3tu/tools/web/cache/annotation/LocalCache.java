package com.y3tu.tools.web.cache.annotation;


import com.y3tu.tools.kit.time.DateUnit;
import com.y3tu.tools.web.cache.setting.ExpireMode;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 本地缓存配置项
 *
 * @author y3tu
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface LocalCache {
    /**
     * 缓存初始Size
     *
     * @return int
     */
    int initialCapacity() default 10;

    /**
     * 缓存最大Size
     *
     * @return int
     */
    int maximumSize() default 5000;

    /**
     * 缓存有效时间
     *
     * @return int
     */
    int expireTime() default 9;

    /**
     * 缓存时间单位
     *
     * @return TimeUnit
     */
    DateUnit dateUnit() default DateUnit.MINUTE;

    /**
     * 缓存失效模式
     *
     * @return ExpireMode
     * @see ExpireMode
     */
    ExpireMode expireMode() default ExpireMode.WRITE;
}
