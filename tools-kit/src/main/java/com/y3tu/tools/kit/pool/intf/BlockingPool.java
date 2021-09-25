package com.y3tu.tools.kit.pool.intf;

import com.y3tu.tools.kit.time.DateUnit;

/**
 * 阻塞对象池接口
 *
 * @param <T> 对象类型
 * @author y3tu
 */
public interface BlockingPool<T> extends Pool<T> {

    /**
     * 获取池中对象 有超时限制
     *
     * @param time     等待时间，如果没有马上获取到对象，可以等待一定时间后获取
     * @param dateUnit 时间单位
     * @return 对象
     * @throws InterruptedException 异常
     */
    T get(long time, DateUnit dateUnit) throws InterruptedException;
}
