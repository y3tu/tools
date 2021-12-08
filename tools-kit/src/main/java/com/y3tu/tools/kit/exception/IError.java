package com.y3tu.tools.kit.exception;

/**
 * 异常枚举接口
 *
 * @author y3tu
 */
public interface IError {

    /**
     * 返回异常code
     *
     * @return code
     */
    String getCode();

    /**
     * 返回异常消息
     *
     * @return message
     */
    String getMessage();
}
