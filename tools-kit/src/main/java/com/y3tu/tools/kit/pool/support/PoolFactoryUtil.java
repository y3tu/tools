package com.y3tu.tools.kit.pool.support;

import com.y3tu.tools.kit.pool.intf.Pool;
import com.y3tu.tools.kit.pool.intf.PoolFactory;
import com.y3tu.tools.kit.time.DateUnit;

/**
 * 对象池创建工厂
 *
 * @author y3tu
 */
public class PoolFactoryUtil {

    /**
     * 创建阻塞的对象池
     *
     * @param coreSize      对象池大小
     * @param maxSize       对象池最大数量
     * @param keepAliveTime 对象存活时间
     * @param unit          时间单位
     * @param poolFactory   对象创建
     * @param <T>           对象类型
     * @return 对象池
     */
    public static <T> BoundedBlockingPool<T> newBoundedBlockingPool(int coreSize, int maxSize, long keepAliveTime, DateUnit unit, PoolFactory<T> poolFactory) {
        return new BoundedBlockingPool<T>(coreSize, maxSize, keepAliveTime, unit, poolFactory);
    }


    public static <T> Pool<T> newBoundedNonBlockingPool(int size, com.y3tu.tools.kit.pool.intf.PoolFactory<T> factory) {
        return new BoundedPool<T>(size, factory);
    }
}
