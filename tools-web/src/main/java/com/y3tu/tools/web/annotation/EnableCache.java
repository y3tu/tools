package com.y3tu.tools.web.annotation;

import com.y3tu.tools.web.cache.configure.CacheConfigure;
import com.y3tu.tools.web.cache.setting.CacheMode;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 开启缓存
 *
 * @author y3tu
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(CacheConfigure.class)
public @interface EnableCache {

    /**
     * 是否开启缓存统计 默认关闭
     */
    boolean stats() default false;

    /**
     * 是否允许缓存空值 默认允许
     */
    boolean allowNullValue() default true;

    /**
     * 缓存模式 默认只开启本地缓存
     */
    CacheMode cacheMode() default CacheMode.ONLY_LOCAL;

    /**
     * redisTemplate的bean名称
     * 开启redis或者多级缓存需要 RedisTemplate<String, Object> 的bean，
     * 默认从spring容器中获取，此属性是加载给定的RedisTemplate实例
     */
    String redisTemplateBeanName() default "";

    /**
     * 是否开启静态数据处理 默认false
     */
    boolean staticData() default false;

    /**
     * 请求接口访问前缀
     * 默认tools-cache
     */
    String api() default "tools-cache";

    /**
     * 静态数据需要扫描的类包
     */
    String[] staticDataPackage() default "";
}
