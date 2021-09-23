package com.y3tu.tools.web.cache.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 将对应数据放到缓存中
 *
 * @author y3tu
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CachePut {

    /**
     * 别名 {@link #cacheName}.
     *
     * @return String[]
     */
    @AliasFor("cacheName")
    String value() default "";

    /**
     * 缓存名称
     *
     * @return String
     */
    @AliasFor("value")
    String cacheName() default "";

    /**
     * 描述
     *
     * @return String
     */
    String description() default "";

    /**
     * 缓存key，支持SpEL表达式
     *
     * @return String
     */
    String key() default "";

    /**
     * 是否忽略在操作缓存中遇到的异常，如反序列化异常，默认true。
     * <p>true: 有异常会输出warn级别的日志，并直接执行被缓存的方法（缓存将失效）</p>
     * <p>false:有异常会输出error级别的日志，并抛出异常</p>
     *
     * @return boolean
     */
    boolean ignoreException() default true;

    /**
     * 本地缓存配置
     *
     * @return LocalCache
     */
    LocalCache localCache() default @LocalCache();

    /**
     * Redis缓存配置
     *
     * @return RedisCache
     */
    RedisCache redisCache() default @RedisCache();
}
