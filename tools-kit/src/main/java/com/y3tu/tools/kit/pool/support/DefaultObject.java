package com.y3tu.tools.kit.pool.support;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 默认对象
 *
 * @author y3tu
 */
@Data
@AllArgsConstructor
public class DefaultObject<T> {
    /**
     * 时间戳
     */
    public long time;
    /**
     * 真正的对象
     */
    T t;
}
