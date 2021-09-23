package com.y3tu.tools.kit.pool.support;

import com.y3tu.tools.kit.pool.intf.Pool;

/**
 * 抽象对象池
 *
 * @param <T> 池中对象类型
 * @author y3tu
 */
public abstract class AbstractPool<T> implements Pool<T> {

    /**
     * 释放对象到池
     *
     * @param t 对象
     */
    @Override
    public final void release(T t) {
        if (isValid(t)) {
            returnPool(t);
        } else {
            handleInvalidReturn(t);
        }
    }

    /**
     * 处理失效的对象
     *
     * @param t 失效对象
     */
    protected abstract void handleInvalidReturn(T t);

    /**
     * 归还对象
     *
     * @param t 对象
     */
    protected abstract void returnPool(T t);

    /**
     * 判断对象是否有效
     *
     * @param t 对象
     * @return 是否有效
     */
    protected abstract boolean isValid(T t);
}
