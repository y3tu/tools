package com.y3tu.tools.web.cache.annotation;

import com.y3tu.tools.web.cache.staticdata.handler.DefaultHandler;
import com.y3tu.tools.web.cache.staticdata.handler.StaticDataHandler;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 加载静态数据注解
 *
 * @author y3tu
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface StaticData {

    /**
     * 别名是 {@link #cacheName}.
     *
     * @return String[]
     */
    @AliasFor("cacheName")
    String value() default "";

    /**
     * 缓存名称，支持SpEL表达式
     *
     * @return String[]
     */
    @AliasFor("value")
    String cacheName() default "";

    /**
     * 缓存key
     *
     * @return
     */
    String key() default "";

    /**
     * 描述
     *
     * @return String
     */
    String depict() default "";

    /**
     * 是否忽略在操作缓存中遇到的异常，如反序列化异常，默认true。
     * <p>true: 有异常会输出warn级别的日志，并直接执行被缓存的方法（缓存将失效）</p>
     * <p>false:有异常会输出error级别的日志，并抛出异常</p>
     *
     * @return boolean
     */
    boolean ignoreException() default true;

    /**
     * 是否随程序启动就加载静态数据
     *
     * @return boolean
     */
    boolean isStartUp() default true;

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

    /**
     * 数据特殊处理
     *
     * @return
     */
    Class<? extends StaticDataHandler> handler() default DefaultHandler.class;
}
