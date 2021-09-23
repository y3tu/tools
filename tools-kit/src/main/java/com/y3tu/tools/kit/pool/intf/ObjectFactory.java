package com.y3tu.tools.kit.pool.intf;

/**
 * 对象创建接口
 *
 * @param <T> 对象类型
 * @author y3tu
 */
public interface ObjectFactory<T> {

    /**
     * 创建一个新的对象
     *
     * @return 新对象
     */
    public abstract T createNew();
}
