package com.y3tu.tools.kit.pool.intf;

/**
 * 池接口
 *
 * @param <T> 池中对象类型
 * @author y3tu
 */
public interface Pool<T> {
    /**
     * 获取池中对象
     *
     * @return 池中对象
     */
    T get();

    /**
     * 释放对象到池
     *
     * @param t 对象
     */
    void release(T t);

    /**
     * 关闭对象池
     */
    void shutdown();
}
