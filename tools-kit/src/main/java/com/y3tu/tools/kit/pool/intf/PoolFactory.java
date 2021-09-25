package com.y3tu.tools.kit.pool.intf;

/**
 * 对象创建接口
 *
 * @param <T> 对象类型
 * @author y3tu
 */
public interface PoolFactory<T> {

    /**
     * 创建一个新的对象
     *
     * @return 新对象
     */
    T createObject();

    /**
     * 校验对象是否有效
     *
     * @param t 对象
     * @return 是否有效
     */
    boolean isValid(T t);

    /**
     * 当对象失效时，作废对象
     *
     * @param t 对象
     */
    void invalidate(T t);
}
