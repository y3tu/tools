package com.y3tu.tools.kit.pool.intf;

/**
 * 对象校验接口
 *
 * @param <T> 对象类型
 * @author y3tu
 */
public interface Validator<T> {
    /**
     * 校验对象是否有效
     *
     * @param t 对象
     * @return 是否有效
     */
    public boolean isValid(T t);

    /**
     * 当对象失效时，作废对象
     *
     * @param t 对象
     */
    public void invalidate(T t);
}
