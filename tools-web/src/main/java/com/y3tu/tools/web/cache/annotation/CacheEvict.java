package com.y3tu.tools.web.cache.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 删除缓存
 *
 * @author y3tu
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CacheEvict {

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
     * 是否删除缓存中所有数据
     * <p>默认情况下是只删除关联key的缓存数据
     * <p>注意：当该参数设置成 {@code true} 时 {@link #key} 参数将无效
     *
     * @return boolean
     */
    boolean allEntries() default false;
}
