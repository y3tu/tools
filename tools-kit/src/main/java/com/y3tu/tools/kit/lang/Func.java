package com.y3tu.tools.kit.lang;

import java.io.Serializable;

/**
 * 函数对象
 * 此接口用于将一个函数包装成为一个对象，从而传递对象
 *
 * @param <P> 参数类型
 * @param <R> 返回值类型
 */
@FunctionalInterface
public interface Func<P, R> extends Serializable {
    /**
     * 执行函数
     *
     * @param params 参数列表
     * @return 函数执行结果
     * @throws Exception 自定义异常
     */
    @SuppressWarnings("unchecked")
    R call(P... params) throws Exception;

    /**
     * 执行函数，异常包装为RuntimeException
     *
     * @param params 参数列表
     * @return 函数执行结果
     */
    @SuppressWarnings("unchecked")
    default R callWithRuntimeException(P... params) {
        try {
            return call(params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
