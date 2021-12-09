package com.y3tu.tools.kit.base;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

/**
 * bean工具类
 *
 * @author y3tu
 */
@Slf4j
public class BeanUtil {
    /**
     * bean 容器
     */
    private static ConcurrentHashMap<Class, Object> beanContainer = new ConcurrentHashMap<>();

    /**
     * 获取bean缓存，如果不存在则创建bean实例并放入容器
     *
     * @param aClass
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> aClass) {
        return (T) beanContainer.computeIfAbsent(aClass, aClass1 -> {
            try {
                return aClass1.newInstance();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            return null;
        });
    }

}
